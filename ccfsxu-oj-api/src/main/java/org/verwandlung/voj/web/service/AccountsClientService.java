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
package org.verwandlung.voj.web.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理用户的登录/注册请求.
 *
 */
@FeignClient(value = "CCUSXU-OJ-DEPT")
@Api(tags = "处理用户的登录/注册请求")
@RequestMapping(value="/accounts")
public interface AccountsClientService {
	/**
	 * 显示用户的登录页面.
	 * @param isLogout - 是否处于登出状态
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 登录信息
	 */
	@RequestMapping(value="/login", method= RequestMethod.POST)
	public ResponseData loginView(
			@RequestParam(value="logout", defaultValue="false") boolean isLogout,
			HttpServletRequest request, HttpServletResponse response);


	/**
	 * 处理用户的登录请求.
	 * @param username - 用户名
	 * @param password - 密码(已使用MD5加密)
	 * @param request - HttpServletRequest对象
	 * @return 一个包含登录验证结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户的登录请求",notes = "")
	@RequestMapping(value="/login.action", method=RequestMethod.POST)
	public @ResponseBody
	ResponseData loginAction(
			@ApiParam(name="username", value = "用户名", example = "zjhzxhz", required = true)
			@RequestParam(value="username") String username,
			@ApiParam(name="password", value = "密码，使用md5进行加密", example = "785ee107c11dfe36de668b1ae7baacbb", required = true)
			@RequestParam(value="password") String password,
			@ApiParam(name="rememberMe", value = "记住我", example = "true", required = true)
			@RequestParam(value="rememberMe") boolean isAutoLoginAllowed,
			HttpServletRequest request);

	/**
	 * 显示用户注册的页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含注册页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "用户注册信息",notes="已经登录则直接跳转到首页")
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public ResponseData registerView(
			HttpServletRequest request, HttpServletResponse response);

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
			HttpServletRequest request);

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
			HttpServletRequest request, HttpServletResponse response);

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
			HttpServletRequest request);

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
			HttpServletRequest request);

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
			HttpServletRequest request, HttpServletResponse response);

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
			HttpServletRequest request);

	/**
	 * 加载用户控制板页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含控制板页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载用户控制板页面")
	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public ResponseData dashboardView(
			HttpServletRequest request, HttpServletResponse response);

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
			HttpServletRequest request);

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
			HttpServletRequest request);

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
			HttpServletRequest request);
}