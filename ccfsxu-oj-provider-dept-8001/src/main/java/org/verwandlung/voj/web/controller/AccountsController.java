/* Verwandlung Online Judge - A cross-platform judge online system
 * Copyright (C) 2018 Haozhe Xie <cshzxie@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`---'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\---/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .'  /--.--\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=---='
 *
 *                          HERE BE BUDDHA
 *
 */
package org.verwandlung.voj.web.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.servlet.view.RedirectView;
import org.verwandlung.voj.web.exception.ResourceNotFoundException;
import org.verwandlung.voj.web.model.Language;
import org.verwandlung.voj.web.model.User;
import org.verwandlung.voj.web.service.LanguageService;
import org.verwandlung.voj.web.service.OptionService;
import org.verwandlung.voj.web.service.SubmissionService;
import org.verwandlung.voj.web.service.UserService;
import org.verwandlung.voj.web.util.*;

/**
 * 处理用户的登录/注册请求.
 *
 */
@Api(tags = "处理用户的登录/注册请求.")
@RestController
@RequestMapping(value="/accounts")
public class AccountsController {
	/**
	 * 显示用户的登录页面.
	 * @param isLogout - 是否处于登出状态
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 登录信息
	 */
	@ApiOperation(value = "用户登录信息查询",notes = "msg为log表示需要登录，为logged表示已登录(直接跳转)")
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public ResponseData loginView(
			@ApiParam(value = "是否登出", name="logout", defaultValue="false", example = "false")
			@RequestParam(value="logout", defaultValue="false") boolean isLogout,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if ( isLogout ) {
			destroySession(request, session);
		}
		String msg;
		if ( isLoggedIn(session) ) {
			msg = "logged";
		} else {
			msg = "need-log";
		}
		return ResponseData.ok().data("msg",msg);
	}

	/**
	 * 为注销的用户销毁Session.
	 * @param request - HttpServletRequest对象
	 * @param session - HttpSession 对象
	 */
	private void destroySession(HttpServletRequest request, HttpSession session) {
		User currentUser = HttpSessionParser.getCurrentUser(request.getSession());
		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		LOGGER.info(String.format("%s logged out at %s", new Object[] {currentUser, ipAddress}));

		session.setAttribute("isLoggedIn", false);
	}

	/**
	 * 检查用户是否已经登录.
	 * @param session - HttpSession 对象
	 * @return 用户是否已经登录
	 */
	private boolean isLoggedIn(HttpSession session) {
		Boolean isLoggedIn = (Boolean)session.getAttribute("isLoggedIn");
		if ( isLoggedIn == null || !isLoggedIn) {
			return false;
		}
		return true;
	}

	/**
	 * 处理用户的登录请求.
	 * @param username - 用户名
	 * @param password - 密码(已使用MD5加密)
	 * @param request - HttpServletRequest对象
	 * @return 一个包含登录验证结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户的登录请求",notes = "")
	@RequestMapping(value="/login.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData loginAction(
			@ApiParam(name="username", value = "用户名", example = "zjhzxhz", required = true)
			@RequestParam(value="username") String username,
			@ApiParam(name="password", value = "密码，使用md5进行加密", example = "785ee107c11dfe36de668b1ae7baacbb", required = true)
			@RequestParam(value="password") String password,
			@ApiParam(name="rememberMe", value = "记住我", example = "true", required = true)
			@RequestParam(value="rememberMe") boolean isAutoLoginAllowed,
			HttpServletRequest request) {
		System.out.println("username = " + username);
		System.out.println("password = " + password);
		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		Map<String, Boolean> result = userService.isAllowedToLogin(username, password);
		LOGGER.info(String.format("User: [Username=%s] tried to log in at %s", new Object[] {username, ipAddress}));
		if ( result.get("isSuccessful") ) {
			User user = userService.getUserUsingUsernameOrEmail(username);
			getSession(request, user, isAutoLoginAllowed);
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 为登录的用户创建Session.
	 * @param request - HttpServletRequest对象
	 * @param user - 一个User对象, 包含用户的基本信息
	 * @param isAutoLoginAllowed - 是否保存登录状态
	 */
	private void getSession(HttpServletRequest request, User user, boolean isAutoLoginAllowed) {
		HttpSession session = request.getSession();
		session.setAttribute("isLoggedIn", true);
		session.setAttribute("isAutoLoginAllowed", isAutoLoginAllowed);
		session.setAttribute("uid", user.getUid());

		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		LOGGER.info(String.format("%s logged in at %s", new Object[] {user, ipAddress}));
	}

	/**
	 * 显示用户注册的页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含注册页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "用户注册信息",notes="已经登录则直接跳转到首页")
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public ResponseData registerView(
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
//		ModelAndView view = null;
		Map<String,Object> result = new HashMap<>();
		if ( isLoggedIn(session) ) {
			result.put("isLoggedIn",true);
//			RedirectView redirectView = new RedirectView(request.getContextPath());
//			redirectView.setExposeModelAttributes(false);
//			view = new ModelAndView(redirectView);
		} else {
			result.put("isLoggedIn",false);
			List<Language> languages = languageService.getAllLanguages();
			boolean isAllowRegister = optionService.getOption("allowUserRegister").getOptionValue().equals("1");

			result.put("languages",languages);
			result.put("isAllowRegister",isAllowRegister);
			result.put("csrfToken",CsrfProtector.getCsrfToken(session));
//			view = new ModelAndView("accounts/register");
//			view.addObject("languages", languages);
//			view.addObject("isAllowRegister", isAllowRegister);
//			view.addObject("csrfToken", CsrfProtector.getCsrfToken(session));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户注册的请求.
	 * @param username - 用户名
	 * @param password - 密码
	 * @param email - 电子邮件地址
	 * @param languageSlug - 偏好语言的别名
	 * @param csrfToken - Csrf的Token
	 * @param request - HttpServletRequest对象
	 * @return 一个包含账户创建结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户注册的请求", notes = "返回注册成功之后的相关信息，返回结果中，successful决定是否成功，各种Legal判断合法true，Empty判断是否为空")
	@RequestMapping(value="/register.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData registerAction(
			@ApiParam(name="username", value = "用户名，超过6位，不重复，不能是数字开头", example = "a13223", required = true)
			@RequestParam(value="username") String username,
			@ApiParam(name="password", value = "密码", example = "12345678", required = true)
			@RequestParam(value="password") String password,
			@ApiParam(name="email", value = "邮箱", example = "1234@qq.com，这种，不重复就行", required = true)
			@RequestParam(value="email") String email,
			@ApiParam(name="trueName", value = "真实姓名", example = "嘟嘟嘟", required = true)
			@RequestParam(value="trueName") String trueName,
			@ApiParam(name="stuId", value = "学号", example = "123456789123", required = true)
			@RequestParam(value="stuId") String stuId,
			@ApiParam(name="languagePreference", value = "语言偏好，从用户注册信息中获取", example = "text/x-csrc", required = true)
			@RequestParam(value="languagePreference") String languageSlug,
			@ApiParam(name="csrfToken", value = "csrfToken，从用户注册信息中获取", required = true)
			@RequestParam(value="csrfToken") String csrfToken,
			HttpServletRequest request) {
		boolean isAllowRegister = optionService.getOption("allowUserRegister").getOptionValue().equals("1");
		boolean isCsrfTokenValid = CsrfProtector.isCsrfTokenValid(csrfToken, request.getSession());
		String userGroupSlug = "users";
		Map<String, Boolean> result = userService.createUser(username, password, email, trueName, stuId,
			userGroupSlug, languageSlug, isCsrfTokenValid, isAllowRegister);
		if ( result.get("isSuccessful") ) {
			User user = userService.getUserUsingUsernameOrEmail(username);
			getSession(request, user, false);
			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			LOGGER.info(String.format("User: [Username=%s] created at %s.",
					new Object[] {username, ipAddress}));
		}
		else {
			LOGGER.warn("Register failed");
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载重置密码页面.
	 * @param email - 用户的电子邮件地址
	 * @param token - 用于重置密码的随机字符串
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含密码重置页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载重置密码页面", notes = "判断是否满足重置密码以及返回重置密码的相关信息，建议先注册一个用户再测试")
	@RequestMapping(value="/reset-password", method=RequestMethod.POST)
	public ResponseData resetPasswordView(
			@ApiParam(value = "邮箱",name="email", required = true)
			@RequestParam(value="email",required = false) String email,
			@ApiParam(value = "token",name="token", required = true)
			@RequestParam(value="token",required = false) String token,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
//		ModelAndView view = null;
		Map<String,Object> result = new HashMap<>();

		if ( isLoggedIn(session) ) {
			result.put("isLoggedIn",true);
//			RedirectView redirectView = new RedirectView(request.getContextPath());
//			redirectView.setExposeModelAttributes(false);
//			view = new ModelAndView(redirectView);
		} else {
			boolean isTokenValid = false;
			if ( token != null && !token.isEmpty() ) {
				isTokenValid = userService.isEmailValidationValid(email, token);
			}
			result.put("isLoggedIn",false);
			result.put("email", email);
			result.put("token", token);
			result.put("isTokenValid", isTokenValid);
			result.put("csrfToken", CsrfProtector.getCsrfToken(session));


//			view = new ModelAndView("accounts/reset-password");
//			view.addObject("email", email);
//			view.addObject("token", token);
//			view.addObject("isTokenValid", isTokenValid);
//			view.addObject("csrfToken", CsrfProtector.getCsrfToken(session));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 发送重置密码的电子邮件.
	 * @param username - 用户的用户名
	 * @param email - 用户的电子邮件地址
	 * @param csrfToken - Csrf的Token
	 * @param request - HttpServletRequest对象
	 * @return 一个包含密码重置邮件发送结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "发送重置密码的电子邮件", notes = "建议先注册一个用户进行测试")
	@RequestMapping(value="/forgotPassword.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData forgotPasswordAction(
			@ApiParam(value = "用户名", name="username")
			@RequestParam(value="username") String username,
			@ApiParam(value = "邮箱", name="email")
			@RequestParam(value="email") String email,
			@ApiParam(value = "csrfToken", name="csrfToken")
			@RequestParam(value="csrfToken") String csrfToken,
			HttpServletRequest request) {
		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		boolean isCsrfTokenValid = CsrfProtector.isCsrfTokenValid(csrfToken, request.getSession());
		Map<String, Boolean> result = userService.sendVerificationEmail(username, email, isCsrfTokenValid);

		if ( result.get("isSuccessful") ) {
			LOGGER.info(String.format("User: [Username=%s] send an email for resetting password at %s.",
							new Object[] {username, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 重置用户密码.
	 * @param email - 用户的电子邮件地址
	 * @param token - 用于重设密码的Token
	 * @param newPassword - 新密码
	 * @param confirmPassword - 确认的密码
	 * @param csrfToken - Csrf的Token
	 * @param request - HttpServletRequest对象
	 * @return 一个包含密码重置结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "重置用户密码", notes = "建议先注册一个用户进行测试")
	@RequestMapping(value="/resetPassword.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData resetPasswordAction(
			@ApiParam(value = "邮箱", name="email")
			@RequestParam(value="email") String email,
			@ApiParam(value = "token", name="token")
			@RequestParam(value="token") String token,
			@ApiParam(value = "新密码", name="newPassword")
			@RequestParam(value="newPassword") String newPassword,
			@ApiParam(value = "确认密码", name="confirmPassword")
			@RequestParam(value="confirmPassword") String confirmPassword,
			@ApiParam(value = "csrfToken", name="csrfToken")
			@RequestParam(value="csrfToken") String csrfToken,
			HttpServletRequest request) {
		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		boolean isCsrfTokenValid = CsrfProtector.isCsrfTokenValid(csrfToken, request.getSession());
		Map<String, Boolean> result = userService.resetPassword(email, token, newPassword, confirmPassword, isCsrfTokenValid);

		if ( result.get("isSuccessful") ) {
			LOGGER.info(String.format("User: [Email=%s] resetted password at %s",
							new Object[] {email, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载用户的个人信息.
	 * @param userId - 用户的唯一标识符
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含用户个人信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载用户的个人信息")
	@RequestMapping(value="/user/{userId}", method=RequestMethod.GET)
	public ResponseData userView(
			@ApiParam(value = "用户id，session中登录用户的id", name = "userId", example = "1000")
			@PathVariable("userId") long userId,
			HttpServletRequest request, HttpServletResponse response) {
		User user = userService.getUserUsingUid(userId);
		if ( user == null || "judgers".equals(user.getUserGroup().getUserGroupSlug()) ) {
			throw new ResourceNotFoundException();
		}

//		ModelAndView view = new ModelAndView("accounts/user");
		Map<String, Object> result = new HashMap<>();
		result.put("user", user);
		result.put("UserMeta", userService.getUserMetaUsingUid(user));

		result.put("submissions", submissionService.getSubmissionOfUser(userId));
		result.put("submissionStats", submissionService.getSubmissionStatsOfUser(userId));

//		view.addObject("user", user);
//		view.addAllObjects(userService.getUserMetaUsingUid(user));
//
//		view.addObject("submissions", submissionService.getSubmissionOfUser(userId));
//		view.addObject("submissionStats", submissionService.getSubmissionStatsOfUser(userId));
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 获取某个用户一段时间内的提交次数.
	 * @param userId - 用户的唯一标识符
	 * @param period - 时间间隔的天数
	 * @param request - HttpServletRequest对象
	 * @return 包含某个用户提交次数与时间的 Map 对象
	 */
	@ApiOperation(value = "获取某个用户一段时间内的提交次数")
	@RequestMapping(value="/getNumberOfSubmissionsOfUsers.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getNumberOfSubmissionsOfUsersAction(
			@ApiParam(value = "用户的唯一标识符", name = "uid", example = "1002")
			@RequestParam(value="uid", required=false, defaultValue="0") long userId,
			@ApiParam(value = "时间间隔的天数", name = "period", example = "10")
			@RequestParam(value="period") int period,
			HttpServletRequest request) {
		if ( userId == 0 ) {
			HttpSession session = request.getSession();
			userId = (Long)session.getAttribute("uid");
		}
		Map<String, Object> submissions = new HashMap<>(2, 1);
		Date today = new Date();
		Date previousDate = DateUtils.getPreviousDate(period);
		Map<String, Long> totalSubmissions = submissionService.getNumberOfSubmissionsUsingDate(previousDate, today, userId, false);
		Map<String, Long> acceptedSubmissions = submissionService.getNumberOfSubmissionsUsingDate(previousDate, today, userId, true);

		submissions.put("totalSubmissions", totalSubmissions);
		submissions.put("acceptedSubmissions", acceptedSubmissions);
		return ResponseData.ok().data("submissions",submissions);
	}

	/**
	 * 加载用户控制板页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含控制板页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载用户控制板页面")
	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public ResponseData dashboardView(
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
//		ModelAndView view = null;
		Map<String, Object> result = new HashMap<>();

		if ( !isLoggedIn(session) ) {
			result.put("isLoggedIn", false);
//			RedirectView redirectView = new RedirectView(request.getContextPath() + "/accounts/login");
//			redirectView.setExposeModelAttributes(false);
//			view = new ModelAndView(redirectView);
		}
		else {
			result.put("isLoggedIn", true);
			long userId = (Long)session.getAttribute("uid");
			User user = userService.getUserUsingUid(userId);
			result.put("user", user);
			result.put("UserMeta", userService.getUserMetaUsingUid(user));
			result.put("submissions", submissionService.getSubmissionOfUser(userId));
		}

//		long userId = (Long)session.getAttribute("uid");
//		User user = userService.getUserUsingUid(userId);
//		view = new ModelAndView("accounts/dashboard");
//		view.addObject("user", user);
//		view.addAllObjects(userService.getUserMetaUsingUid(user));
//		view.addObject("submissions", submissionService.getSubmissionOfUser(userId));
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户修改密码的请求.
	 * @param oldPassword - 旧密码
	 * @param newPassword - 新密码
	 * @param confirmPassword - 确认新密码
	 * @param request - HttpServletRequest对象
	 * @return 一个包含密码验证结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户修改密码的请求", notes = "建议注册一个用户进行测试")
	@RequestMapping(value="/changePassword.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData changePasswordInDashboardAction(
			@ApiParam(value = "旧密码", name="oldPassword")
			@RequestParam(value="oldPassword") String oldPassword,
			@ApiParam(value = "新密码", name="newPassword")
			@RequestParam(value="newPassword") String newPassword,
			@ApiParam(value = "确认密码", name="confirmPassword")
			@RequestParam(value="confirmPassword") String confirmPassword,
			HttpServletRequest request) {
		User currentUser = HttpSessionParser.getCurrentUser(request.getSession());
		String ipAddress = HttpRequestParser.getRemoteAddr(request);

		Map<String, Boolean> result = userService.changePassword(currentUser, oldPassword, newPassword, confirmPassword);
		if ( result.get("isSuccessful") ) {
			LOGGER.info(String.format("%s changed password at %s", new Object[] {currentUser, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户更改个人资料的请求.
	 * @param email - 用户的电子邮件地址
	 * @param location - 用户的所在地区
	 * @param website - 用户的个人主页
	 * @param socialLinks - 用户的社交网络信息
	 * @param aboutMe - 用户的个人简介
	 * @param request - HttpServletRequest对象
	 * @return 一个包含个人资料修改结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户更改个人资料的请求", notes = "建议注册一个用户进行测试")
	@RequestMapping(value="/updateProfile.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData updateProfileInDashboardAction(
			@ApiParam(value = "邮箱", name="email")
			@RequestParam(value="email") String email,
			@ApiParam(value = "地址", name="location")
			@RequestParam(value="location") String location,
			@ApiParam(value = "主页", name="website")
			@RequestParam(value="website") String website,
			@ApiParam(value = "社交网络信息", name="socialLinks")
			@RequestParam(value="socialLinks") String socialLinks,
			@ApiParam(value = "个人简介", name="aboutMe")
			@RequestParam(value="aboutMe") String aboutMe,
			HttpServletRequest request) {
		User currentUser = HttpSessionParser.getCurrentUser(request.getSession());
		String ipAddress = HttpRequestParser.getRemoteAddr(request);

		Map<String, Boolean> result = userService.updateProfile(currentUser, email, location, website, socialLinks, aboutMe);
		if ( result.get("isSuccessful") ) {
			LOGGER.info(String.format("%s updated profile at %s", new Object[] {currentUser, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户修改头像的请求
	 * @param file -头像文件
	 * @param userId -用户编号
	 * @param request - HttpServletRequest对象
	 * @return
	 */
	@ApiOperation(value = "处理用户修改头像的请求", notes = "建议注册一个用户进行测试")
	@RequestMapping(value = "/changeAvatar", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData changeAvatar(
			@ApiParam(value = "头像文件", name="avatar_file")
			@RequestParam(value="avatar_file") MultipartFile file,
			@ApiParam(value = "用户编号", name="userId")
			@RequestParam(value="userId") long userId,
			HttpServletRequest request) {
		LOGGER.debug("get in the changeAvatar");
		User currentUser = HttpSessionParser.getCurrentUser(request.getSession());

		if (!file.isEmpty()) {
			try {
				// 文件存放服务端的位置
				String rootPath = avatarsPath;
				File dir = new File(rootPath);
				if (!dir.exists()) {
					LOGGER.debug("!dir.exists()");
					dir.mkdirs();
				}
				// 写文件到服务器
				String fileName = file.getOriginalFilename();
				fileName = userId+"."+fileName.substring(fileName.lastIndexOf(".") + 1);
				LOGGER.debug("fileName = kkkk  " + fileName);
				String avatarUrl = "/avatars/"+fileName;
				assert currentUser != null;
				userService.updateAvatar(currentUser,avatarUrl);
				File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
				file.transferTo(serverFile);
				LOGGER.debug("write it");
				System.out.println("write it");
			} catch (Exception e) {
				LOGGER.debug("e.getMessage() = kkkkkkk "+e.getMessage());
				return ResponseData.error();
			}
		}
		LOGGER.debug("changeAvatar Successful");
		return ResponseData.ok().data("result",true);
	}

	/**
	 * 自动注入的UserService对象.
	 * 用于完成用户业务逻辑操作.
	 */
	@Autowired
	private UserService userService;

	/**
	 * 自动注入的LanguageService对象.
	 * 用于加载注册页面的语言选项.
	 */
	@Autowired
	private LanguageService languageService;

	/**
	 * 自动注入的SubmissionService对象.
	 * 用于加载个人信息页面用户的提交和通过情况.
	 */
	@Autowired
	private SubmissionService submissionService;

	/**
	 * 自动注入的OptionService对象.
	 * 用于查询注册功能是否开放.
	 */
	@Autowired
	private OptionService optionService;

	@Value("${cbs.imagesPath}")
	private String avatarsPath;

	/**
	 * 日志记录器.
	 */
	private static final Logger LOGGER = LogManager.getLogger(AccountsController.class);
}