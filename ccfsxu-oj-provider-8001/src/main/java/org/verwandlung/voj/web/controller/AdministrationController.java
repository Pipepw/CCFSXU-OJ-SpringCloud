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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import org.verwandlung.voj.web.exception.ResourceNotFoundException;
import org.verwandlung.voj.web.messenger.ApplicationEventListener;
import org.verwandlung.voj.web.model.*;
import org.verwandlung.voj.web.service.*;
import org.verwandlung.voj.web.util.*;

/**
 * 用于处理系统管理的请求.
 *
 */
@Api(tags = "用于处理系统管理的请求.")
@RestController
@RequestMapping(value="/administration")
public class AdministrationController {
	/**
	 * 加载系统管理首页.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含系统管理页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载系统管理首页", notes = "先登陆管理员账号")
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseData indexView(
			HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView view = new ModelAndView("administration/index");
//		view.addObject("totalUsers", getTotalUsers());
//		view.addObject("newUsersToday", getNumberOfUserRegisteredToday());
//		view.addObject("onlineUsers", getOnlineUsers());
//		view.addObject("totalProblems", getTotalProblems());
//		view.addObject("numberOfCheckpoints", getNumberOfCheckpoints());
//		view.addObject("privateProblems", getPrivateProblems());
//		view.addObject("submissionsToday", getSubmissionsToday());
//		view.addObject("memoryUsage", getCurrentMemoryUsage());
//		view.addObject("onlineJudgers", getOnlineJudgers());

		Map<String, Object> result = new HashMap<>();
		result.put("totalUsers", getTotalUsers());
		result.put("newUsersToday", getNumberOfUserRegisteredToday());
		result.put("onlineUsers", getOnlineUsers());
		result.put("totalProblems", getTotalProblems());
		result.put("numberOfCheckpoints", getNumberOfCheckpoints());
		result.put("privateProblems", getPrivateProblems());
		result.put("submissionsToday", getSubmissionsToday());
		result.put("memoryUsage", getCurrentMemoryUsage());
		result.put("onlineJudgers", getOnlineJudgers());
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 获取系统中注册用户的总数.
	 * @return 系统中注册用户的总数
	 */
	private long getTotalUsers() {
		UserGroup userGroup = userService.getUserGroupUsingSlug("users");
		return userService.getNumberOfUsers(userGroup);
	}
	
	/**
	 * 获取今日注册的用户数量.
	 * @return 今日注册的用户数量
	 */
	public long getNumberOfUserRegisteredToday() {
		return userService.getNumberOfUserRegisteredToday();
	}
	
	/**
	 * 获取在线用户的数量.
	 * @return 在线用户的数量
	 */
	private long getOnlineUsers() {
		return SessionListener.getTotalSessions();
	}
	
	/**
	 * 获取全部试题的总数量.
	 * @return 全部试题的总数量
	 */
	private long getTotalProblems() {
		return problemService.getNumberOfProblems();
	}
	
	/**
	 * 获取私有试题的数量.
	 * @return 私有试题的数量
	 */
	private long getPrivateProblems() {
		long numberOfTotalProblems = getTotalProblems();
		long numberOfPublicProblems = problemService.getNumberOfProblemsUsingFilters(null, "", true);
		return numberOfTotalProblems - numberOfPublicProblems;
	}
	
	/**
	 * 获取全部试题测试点的数量(包括私有试题).
	 * @return 全部试题测试点的数量
	 */
	private long getNumberOfCheckpoints() {
		return problemService.getNumberOfCheckpoints();
	}

	/**
	 * 获取今日的提交数量.
	 * @return 今日的提交数量
	 */
	private long getSubmissionsToday() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		
		calendar.set(year, month, date, 0, 0, 0);
		Date startTime = calendar.getTime();
		
		calendar.set(year, month, date + 1, 0, 0, 0);
		Date endTime = calendar.getTime();
		return submissionService.getNumberOfSubmissionsUsingDate(startTime, endTime);
	}
	
	/**
	 * 获取Web应用当前内存占用情况.
	 * @return Web应用当前内存占用(MB)
	 */
	private long getCurrentMemoryUsage() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		
		return (totalMemory - freeMemory) / 1048576;
	}
	
	/**
	 * 获取在线的评测机数量.
	 * 通过获取监听消息队列的Consumer数量.
	 * @return 在线的评测机数量
	 */
	private long getOnlineJudgers() {
		return eventListener.getOnlineJudgers();
	}
	
	/**
	 * 获取系统一段时间内的提交次数.
	 * @param period - 时间间隔的天数
	 * @param request - HttpServletRequest对象
	 * @return 包含提交次数与时间的 Map 对象
	 */
	@ApiOperation(value = "获取系统一段时间内的提交次数")
	@RequestMapping(value="/getNumberOfSubmissions.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getNumberOfSubmissionsAction(
			@ApiParam(value = "时间间隔的天数", name = "period", example = "10")
			@RequestParam(value="period") int period,
			HttpServletRequest request) {
		Map<String, Object> submissions = new HashMap<>(2, 1);
		Date today = new Date();
		Date previousDate = DateUtils.getPreviousDate(period);
		Map<String, Long> totalSubmissions = submissionService.getNumberOfSubmissionsUsingDate(previousDate, today, 0, false);
		Map<String, Long> acceptedSubmissions = submissionService.getNumberOfSubmissionsUsingDate(previousDate, today, 0, true);
		
		submissions.put("totalSubmissions", totalSubmissions);
		submissions.put("acceptedSubmissions", acceptedSubmissions);
		return ResponseData.ok().data("submissions",submissions);
	}
	
	/**
	 * 加载用户列表页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含用户列表页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载用户列表页面.")
	@RequestMapping(value="/all-users", method=RequestMethod.GET)
	public ResponseData allUsersView(
			@ApiParam(value = "用户分组", name="userGroup", required=false, defaultValue="")
			@RequestParam(value="userGroup", required=false, defaultValue="") String userGroupSlug,
			@ApiParam(value = "用户名", name="username", required=false, defaultValue="")
			@RequestParam(value="username", required=false, defaultValue="") String username,
			@ApiParam(value = "页号", name="page", required=false, defaultValue="1")
			@RequestParam(value="page", required=false, defaultValue="1") long pageNumber,
			HttpServletRequest request, HttpServletResponse response) {
		final int NUMBER_OF_USERS_PER_PAGE = 100;
		List<UserGroup> userGroups = userService.getUserGroups();
		UserGroup userGroup = userService.getUserGroupUsingSlug(userGroupSlug);
		long totalUsers = userService.getNumberOfUsersUsingUserGroupAndUsername(userGroup, username);
		long offset = (pageNumber >= 1 ? pageNumber - 1 : 0) * NUMBER_OF_USERS_PER_PAGE;
		List<User> users = userService.getUserUsingUserGroupAndUsername(userGroup, username, offset, NUMBER_OF_USERS_PER_PAGE);
		
//		ModelAndView view = new ModelAndView("administration/all-users");
//		view.addObject("userGroups", userGroups);
//		view.addObject("selectedUserGroup", userGroupSlug);
//		view.addObject("username", username);
//		view.addObject("currentPage", pageNumber);
//		view.addObject("totalPages", (long) Math.ceil(totalUsers * 1.0 / NUMBER_OF_USERS_PER_PAGE));
//		view.addObject("users", users);

		Map<String, Object> result = new HashMap<>();
		result.put("userGroups", userGroups);
		result.put("selectedUserGroup", userGroupSlug);
		result.put("username", username);
		result.put("currentPage", pageNumber);
		result.put("totalPages", (long) Math.ceil(totalUsers * 1.0 / NUMBER_OF_USERS_PER_PAGE));
		result.put("users", users);
		for (User user : users) {
			System.out.println("stuId = "+user.getStuId());
		}
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 删除选定的用户.
	 * @param users - 用户ID的集合, 以逗号(, )分隔
	 * @param request - HttpServletRequest对象
	 * @return 提交记录的删除结果
	 */
	@ApiOperation(value = "删除选定的用户", notes = "建议新建用户之后测试")
	@RequestMapping(value="/deleteUsers.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData deleteUsersAction(
			@ApiParam(value = "用户ID的集合, 以逗号(, )分隔", name="users")
			@RequestParam(value="users") String users,
			HttpServletRequest request) {
		Map<String, Boolean> result = new HashMap<>(2, 1);
		List<Long> userList = JSON.parseArray(users, Long.class);
		
		for ( Long userId : userList ) {
			userService.deleteUser(userId);
		}
		result.put("isSuccessful", true);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载编辑用户信息的页面.
	 * @param userId - 用户的唯一标识符
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含编辑用户信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载编辑用户信息的页面", notes = "建议新建用户后测试")
	@RequestMapping(value="/edit-user/{userId}", method=RequestMethod.GET)
	public ResponseData editUserView(
			@ApiParam(value = "用户的唯一标识符", name = "userId")
			@PathVariable(value = "userId") long userId,
			HttpServletRequest request, HttpServletResponse response) {
		User user = userService.getUserUsingUid(userId);
		Map<String, Object> userMeta = userService.getUserMetaUsingUid(user);
		if ( user == null ) {
			throw new ResourceNotFoundException();
		}

		List<UserGroup> userGroups = userService.getUserGroups();
		List<Language> languages = languageService.getAllLanguages();
//		ModelAndView view = new ModelAndView("administration/edit-user");
//		view.addObject("user", user);
//		view.addAllObjects(userMeta);
//		view.addObject("userGroups", userGroups);
//		view.addObject("languages", languages);

		Map<String, Object> result = new HashMap<>();
		result.put("user", user);
		result.put("userMeta",userMeta);
		result.put("userGroups", userGroups);
		result.put("languages", languages);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 编辑用户个人信息.
	 * @param uid - 用户的唯一标识符.
	 * @param password - 用户的密码(未经MD5加密)
	 * @param email - 用户的电子邮件地址
	 * @param userGroupSlug - 用户组的别名
	 * @param preferLanguageSlug - 用户的偏好语言的别名
	 * @param location - 用户的所在地区
	 * @param website - 用户的个人主页
	 * @param socialLinks - 用户的社交网络信息
	 * @param aboutMe - 用户的个人简介
	 * @param request - HttpServletRequest对象
	 * @return 一个包含个人资料修改结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "编辑用户个人信息", notes = "建议新建用户后测试")
	@RequestMapping(value="/editUser.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData editUserAction(
			@ApiParam(value="用户唯一标识符", name="uid")
			@RequestParam(value="uid") long uid,
			@ApiParam(value="用户的密码", name="password")
			@RequestParam(value="password") String password,
			@ApiParam(value="用户邮箱", name="email")
			@RequestParam(value="email") String email,
			@ApiParam(value="用户分组", name="userGroup")
			@RequestParam(value="userGroup") String userGroupSlug,
			@ApiParam(value="用户偏好语言", name="preferLanguage")
			@RequestParam(value="preferLanguage") String preferLanguageSlug,
			@ApiParam(value="用户地址", name="location")
			@RequestParam(value="location") String location,
			@ApiParam(value="用户个人主页", name="website")
			@RequestParam(value="website") String website,
			@ApiParam(value="用户社交网络信息", name="socialLinks")
			@RequestParam(value="socialLinks") String socialLinks,
			@ApiParam(value="用户个人简介", name="aboutMe")
			@RequestParam(value="aboutMe") String aboutMe,
			HttpServletRequest request) {
		User user = userService.getUserUsingUid(uid);
		Map<String, Boolean> result = new HashMap<>(12, 1);
		result.put("isSuccessful", false);
		result.put("isUserExists", false);

		if ( user != null ) {
			Map<String, Boolean> updateProfileResult = userService.updateProfile(user, password, userGroupSlug, preferLanguageSlug);
			Map<String, Boolean> updateUserMetaResult = userService.updateProfile(user, email, location, website, socialLinks, aboutMe);
			boolean isUpdateProfileSuccessful = updateProfileResult.get("isSuccessful");
			boolean isUpdateUserMetaSuccessful = updateUserMetaResult.get("isSuccessful");

			result.putAll(updateProfileResult);
			result.putAll(updateUserMetaResult);
			result.put("isUserExists", true);
			result.put("isSuccessful", isUpdateProfileSuccessful && isUpdateUserMetaSuccessful);
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载创建用户页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含创建用户页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载创建用户页面")
	@RequestMapping(value="/new-user", method=RequestMethod.GET)
	public ResponseData newUserView(
			HttpServletRequest request, HttpServletResponse response) {
		List<UserGroup> userGroups = userService.getUserGroups();
		List<Language> languages = languageService.getAllLanguages();
//		ModelAndView view = new ModelAndView("administration/new-user");
//		view.addObject("userGroups", userGroups);
//		view.addObject("languages", languages);
		Map<String, Object> result = new HashMap<>();
		result.put("userGroups", userGroups);
		result.put("languages", languages);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 创建新用户.
	 * @param username - 用户名
	 * @param password - 密码
	 * @param email - 电子邮件地址
	 * @param userGroupSlug - 用户组的别名
	 * @param preferLanguageSlug - 偏好语言的别名
	 * @param request - HttpServletRequest对象
	 * @return 一个包含账户创建结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "创建新用户", notes = "根据结果中的isSuccessful判断是否成功")
	@RequestMapping(value="/newUser.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData newUserAction(
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
			@ApiParam(value = "用户组的别名，从创建用户页面中获取信息", name="userGroup", example = "users", required = true)
			@RequestParam(value="userGroup") String userGroupSlug,
			@ApiParam(value = "偏好语言的别名，从创建用户页面中获取信息", name="preferLanguage", example = "text/x-csrc", required = true)
			@RequestParam(value="preferLanguage") String preferLanguageSlug,
			HttpServletRequest request) {
		System.out.println("username = " + username);
		Map<String, Boolean> result = userService.createUser(username, password, email, trueName, stuId, userGroupSlug, preferLanguageSlug);

		if ( result.get("isSuccessful") ) {
			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			LOGGER.info(String.format("User: [Username=%s] was created by administrator at %s.",
					new Object[] {username, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载试题列表页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含提交列表页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载试题列表页面")
	@RequestMapping(value="/all-problems", method=RequestMethod.GET)
	public ResponseData allProblemsView(
			@ApiParam(value="关键字", name="keyword", required=false, defaultValue="")
			@RequestParam(value="keyword", required=false, defaultValue="") String keyword,
			@ApiParam(value="问题分类", name="problemCategory", required=false, defaultValue="")
			@RequestParam(value="problemCategory", required=false, defaultValue="") String problemCategorySlug,
			@ApiParam(value="问题标签", name="problemTag", required=false, defaultValue="")
			@RequestParam(value="problemTag", required=false, defaultValue="") String problemTagSlug,
			@ApiParam(value="页号", name="page", required=false, defaultValue="1")
			@RequestParam(value="page", required=false, defaultValue="1") long pageNumber,
			HttpServletRequest request, HttpServletResponse response) {
		final int NUMBER_OF_PROBLEMS_PER_PAGE = 100;
		List<ProblemCategory> problemCategories = problemService.getProblemCategories();
		long totalProblems = problemService.getNumberOfProblemsUsingFilters(keyword, problemCategorySlug, false);

		long offset = (pageNumber >= 1 ? pageNumber - 1 : 0) * NUMBER_OF_PROBLEMS_PER_PAGE;
		long problemIdLowerBound = problemService.getFirstIndexOfProblems() + offset;
		long problemIdUpperBound = problemIdLowerBound + NUMBER_OF_PROBLEMS_PER_PAGE - 1;

		List<Problem> problems = problemService.getProblemsUsingFilters(problemIdLowerBound, keyword, problemCategorySlug, problemTagSlug, false, NUMBER_OF_PROBLEMS_PER_PAGE);
		Map<Long, List<ProblemCategory>> problemCategoryRelationships =
				problemService.getProblemCategoriesOfProblems(problemIdLowerBound, problemIdUpperBound);
		Map<Long, List<ProblemTag>> problemTagRelationships =
				problemService.getProblemTagsOfProblems(problemIdLowerBound, problemIdUpperBound);

//		ModelAndView view = new ModelAndView("administration/all-problems");
//		view.addObject("problemCategories", problemCategories);
//		view.addObject("selectedProblemCategory", problemCategorySlug);
//		view.addObject("keyword", keyword);
//		view.addObject("currentPage", pageNumber);
//		view.addObject("totalPages", (long) Math.ceil(totalProblems * 1.0 / NUMBER_OF_PROBLEMS_PER_PAGE));
//		view.addObject("problems", problems);
//		view.addObject("problemCategoryRelationships", problemCategoryRelationships);
//		view.addObject("problemTagRelationships", problemTagRelationships);
		Map<String, Object> result = new HashMap<>();
		result.put("problemCategories", problemCategories);
		result.put("selectedProblemCategory", problemCategorySlug);
		result.put("keyword", keyword);
		result.put("currentPage", pageNumber);
		result.put("totalPages", (long) Math.ceil(totalProblems * 1.0 / NUMBER_OF_PROBLEMS_PER_PAGE));
		result.put("problems", problems);
		result.put("problemCategoryRelationships", problemCategoryRelationships);
		result.put("problemTagRelationships", problemTagRelationships);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 删除选定的试题.
	 * @param problems - 试题ID的集合, 以逗号(, )分隔
	 * @param request - HttpServletRequest对象
	 * @return 试题的删除结果
	 */
	@ApiOperation(value = "删除选定的试题", notes = "查询试题后填入")
	@RequestMapping(value="/deleteProblems.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData deleteProblemsAction(
			@ApiParam(value="选定的问题", name="problems", required = true)
			@RequestParam(value="problems") String problems,
			HttpServletRequest request) {
		Map<String, Boolean> result = new HashMap<>(2, 1);
		List<Long> problemList = JSON.parseArray(problems, Long.class);

		for ( Long problemId : problemList ) {
			problemService.deleteProblem(problemId);

			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			LOGGER.info(String.format("Problem: [ProblemId=%s] was deleted by administrator at %s.",
					new Object[] {problemId, ipAddress}));
		}
		result.put("isSuccessful", true);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载创建试题页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含创建试题页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载创建试题页面")
	@RequestMapping(value="/new-problem", method=RequestMethod.GET)
	public ResponseData newProblemView(
			HttpServletRequest request, HttpServletResponse response) {
		Map<ProblemCategory, List<ProblemCategory>> problemCategories = problemService.getProblemCategoriesWithHierarchy();

//		ModelAndView view = new ModelAndView("administration/new-problem");
//		view.addObject("problemCategories", problemCategories);
		Map<String, Object> result = new HashMap<>();
		result.put("problemCategories", problemCategories);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户创建试题的请求.
	 * @param problemName - 试题名称
	 * @param timeLimit - 时间限制
	 * @param memoryLimit - 内存占用限制
	 * @param description - 试题描述
	 * @param hint - 试题提示
	 * @param inputFormat - 输入格式
	 * @param outputFormat - 输出格式
	 * @param inputSample - 输入样例
	 * @param outputSample - 输出样例
	 * @param testCases - 测试用例(JSON 格式)
	 * @param problemCategories - 试题分类(JSON 格式)
	 * @param problemTags - 试题标签((JSON 格式)
	 * @param isPublic - 试题是否公开
	 * @param isExactlyMatch - 测试点是否精确匹配
	 * @param request - HttpServletRequest对象
	 * @return 包含试题创建结果的 Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户创建试题的请求")
	@RequestMapping(value="/createProblem.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createProblemAction(
			@ApiParam(value="试题名称", name="problemName")
			@RequestParam(value="problemName") String problemName,
			@ApiParam(value="时间限制", name="timeLimit")
			@RequestParam(value="timeLimit") String timeLimit,
			@ApiParam(value="内存限制", name="memoryLimit")
			@RequestParam(value="memoryLimit") String memoryLimit,
			@ApiParam(value="试题描述", name="description")
			@RequestParam(value="description") String description,
			@ApiParam(value="提示", name="hint")
			@RequestParam(value="hint") String hint,
			@ApiParam(value="输入格式", name="inputFormat")
			@RequestParam(value="inputFormat") String inputFormat,
			@ApiParam(value="输出格式", name="outputFormat")
			@RequestParam(value="outputFormat") String outputFormat,
			@ApiParam(value="样例输入", name="inputSample")
			@RequestParam(value="inputSample") String inputSample,
			@ApiParam(value="样例输出", name="outputSample")
			@RequestParam(value="outputSample") String outputSample,
			@ApiParam(value="测试用例(JSON 格式)", name="testCases")
			@RequestParam(value="testCases") String testCases,
			@ApiParam(value="试题分类(JSON 格式)", name="problemCategories")
			@RequestParam(value="problemCategories") String problemCategories,
			@ApiParam(value="试题标签((JSON 格式)", name="problemTags")
			@RequestParam(value="problemTags") String problemTags,
			@ApiParam(value="是否公开", name="isPublic")
			@RequestParam(value="isPublic") boolean isPublic,
			@ApiParam(value="测试点是否精确匹配", name="isExactlyMatch")
			@RequestParam(value="isExactlyMatch") boolean isExactlyMatch,
			HttpServletRequest request) {
		if ( timeLimit.isEmpty() || !StringUtils.isNumeric(timeLimit) ) {
			timeLimit = "-1";
		}
		if ( memoryLimit.isEmpty() || !StringUtils.isNumeric(memoryLimit) ) {
			memoryLimit = "-1";
		}
		Map<String, Object> result = problemService.createProblem(problemName, Integer.parseInt(timeLimit),
				Integer.parseInt(memoryLimit), description, hint, inputFormat, outputFormat, inputSample,
				outputSample, testCases, problemCategories, problemTags, isPublic, isExactlyMatch);
		if ( (boolean) result.get("isSuccessful") ) {
			long problemId = (Long) result.get("problemId");
			String ipAddress = HttpRequestParser.getRemoteAddr(request);

			LOGGER.info(String.format("Problem: [ProblemId=%s] was created by administrator at %s.",
					new Object[] {problemId, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户导入试题的请求
	 *  @param file - 导入的xml文件
	 * @param request - HttpServletRequest对象
	 * @return 包含试题创建结果的 Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户导入试题的请求")
	@RequestMapping(value="/importProblem", method=RequestMethod.POST)
	public ResponseData importProblemAction(
			@ApiParam(value="导入的xml文件", name="problem_file")
			@RequestParam(value="problem_file") MultipartFile file,
			HttpServletRequest request) {
		Map<String, Object> result=null;
		if (!file.isEmpty()) {
			try {
				// 文件存放服务端的位置
				String rootPath = ResourceUtils.getURL("classpath:").getPath();
				File dir = new File(rootPath + File.separator + "fpsProblems");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 写文件到服务器
				File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
				file.transferTo(serverFile);
				 result = fpsProblemService.FPStoProblems(rootPath + File.separator + "fpsProblems" + File.separator + file.getOriginalFilename());
				if ( (boolean) result.get("isSuccessful") ) {
					long problemId = (Long) result.get("problemId");
					String ipAddress = HttpRequestParser.getRemoteAddr(request);

					LOGGER.info(String.format("Problem: [ProblemId=%s] was created by administrator at %s.",
							new Object[] {problemId, ipAddress}));
				}
//				System.out.println("write it");
			} catch (Exception e) {
//				System.out.println("no file");
			}
		}
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 加载编辑试题页面.
	 * @param problemId - 试题的唯一标识符
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含提交列表页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载编辑试题页面")
	@RequestMapping(value="/edit-problem/{problemId}", method=RequestMethod.GET)
	public ResponseData editProblemsView(
			@PathVariable(value = "problemId") long problemId,
			HttpServletRequest request, HttpServletResponse response) {
		Problem problem = problemService.getProblem(problemId);
		
		if ( problem == null ) {
			throw new ResourceNotFoundException();
		}
		List<Checkpoint> checkpoints = problemService.getCheckpointsUsingProblemId(problemId);
		List<ProblemCategory> selectedProblemCategories = problemService.getProblemCategoriesUsingProblemId(problemId);
		Map<ProblemCategory, List<ProblemCategory>> problemCategories = problemService.getProblemCategoriesWithHierarchy();
		List<ProblemTag> problemTags = problemService.getProblemTagsUsingProblemId(problemId);
		
//		ModelAndView view = new ModelAndView("administration/edit-problem");
//		view.addObject("problem", problem);
//		view.addObject("checkpoints", checkpoints);
//		view.addObject("problemCategories", problemCategories);
//		view.addObject("selectedProblemCategories", selectedProblemCategories);
//		view.addObject("problemTags", problemTags);

		Map<String, Object> result = new HashMap<>();
		result.put("problem", problem);
		result.put("checkpoints", checkpoints);
		result.put("problemCategories", problemCategories);
		result.put("selectedProblemCategories", selectedProblemCategories);
		result.put("problemTags", problemTags);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 处理用户编辑试题的请求.
	 * @param problemName - 试题名称
	 * @param timeLimit - 时间限制
	 * @param memoryLimit - 内存占用限制
	 * @param description - 试题描述
	 * @param hint - 试题提示
	 * @param inputFormat - 输入格式
	 * @param outputFormat - 输出格式
	 * @param inputSample - 输入样例
	 * @param outputSample - 输出样例
	 * @param testCases - 测试用例(JSON 格式)
	 * @param problemCategories - 试题分类(JSON 格式)
	 * @param problemTags - 试题标签((JSON 格式)
	 * @param isPublic - 试题是否公开
	 * @param isExactlyMatch - 测试点是否精确匹配
	 * @param request - HttpServletRequest对象
	 * @return 包含试题编辑结果的 Map<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户编辑试题的请求", notes = "根据结果中的isSuccessful判断是否成功")
	@RequestMapping(value="/editProblem.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData editProblemAction(
			@ApiParam(value="试题的唯一标识符", name="problemId")
			@RequestParam(value="problemId") long problemId,
			@ApiParam(value="试题名称", name="problemName")
			@RequestParam(value="problemName") String problemName,
			@ApiParam(value="时间限制", name="timeLimit")
			@RequestParam(value="timeLimit") String timeLimit,
			@ApiParam(value="内存限制", name="memoryLimit")
			@RequestParam(value="memoryLimit") String memoryLimit,
			@ApiParam(value="试题描述", name="description")
			@RequestParam(value="description") String description,
			@ApiParam(value="提示", name="hint")
			@RequestParam(value="hint") String hint,
			@ApiParam(value="输入格式", name="inputFormat")
			@RequestParam(value="inputFormat") String inputFormat,
			@ApiParam(value="输出格式", name="outputFormat")
			@RequestParam(value="outputFormat") String outputFormat,
			@ApiParam(value="样例输入", name="inputSample")
			@RequestParam(value="inputSample") String inputSample,
			@ApiParam(value="样例输出", name="outputSample")
			@RequestParam(value="outputSample") String outputSample,
			@ApiParam(value="测试点", name="testCases")
			@RequestParam(value="testCases") String testCases,
			@ApiParam(value="试题分类", name="problemCategories")
			@RequestParam(value="problemCategories") String problemCategories,
			@ApiParam(value="试题标签", name="problemTags")
			@RequestParam(value="problemTags") String problemTags,
			@ApiParam(value="是否公开", name="isPublic")
			@RequestParam(value="isPublic") boolean isPublic,
			@ApiParam(value="测试点是否精确匹配", name="isExactlyMatch")
			@RequestParam(value="isExactlyMatch") boolean isExactlyMatch,
			HttpServletRequest request) {
		if ( timeLimit.isEmpty() || !StringUtils.isNumeric(timeLimit) ) {
			timeLimit = "-1";
		}
		if ( memoryLimit.isEmpty() || !StringUtils.isNumeric(memoryLimit) ) {
			memoryLimit = "-1";
		}
		Map<String, Boolean> result = problemService.editProblem(problemId, problemName, Integer.parseInt(timeLimit), 
				Integer.parseInt(memoryLimit), description, hint, inputFormat, outputFormat, inputSample, 
				outputSample, testCases, problemCategories, problemTags, isPublic, isExactlyMatch);
		
		if ( result.get("isSuccessful") ) {
			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			
			LOGGER.info(String.format("Problem: [ProblemId=%s] was edited by administrator at %s.", 
					new Object[] {problemId, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 加载试题分类页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含试题分类页面信息的ModelAndView对象.
	 */
	@ApiOperation(value = "加载试题分类页面")
	@RequestMapping(value="/problem-categories", method=RequestMethod.GET)
	public ResponseData problemCategoriesView(
			HttpServletRequest request, HttpServletResponse response) {
		List<ProblemCategory> problemCategories = problemService.getProblemCategories();
		
//		ModelAndView view = new ModelAndView("administration/problem-categories");
//		view.addObject("problemCategories", problemCategories);
		Map<String, Object> result = new HashMap<>();
		result.put("problemCategories", problemCategories);
		return ResponseData.ok().data("result", result);
	}
	
	/**
	 * 创建试题分类.
	 * @param problemCategorySlug - 试题分类的别名
	 * @param problemCategoryName - 试题分类的名称
	 * @param parentProblemCategorySlug - 父级试题分类的别名
	 * @param request - HttpServletRequest对象
	 * @return 包含试题分类的创建结果的Map<String, Object>对象
	 */
	@ApiOperation(value = "创建试题分类")
	@RequestMapping(value="/createProblemCategory.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createProblemCategoryAction(
			@ApiParam(value="试题分类别名", name="problemCategorySlug")
			@RequestParam(value="problemCategorySlug") String problemCategorySlug,
			@ApiParam(value="试题分类名称", name="problemCategoryName")
			@RequestParam(value="problemCategoryName") String problemCategoryName,
			@ApiParam(value="父级试题分类别名", name="parentProblemCategory")
			@RequestParam(value="parentProblemCategory") String parentProblemCategorySlug,
			HttpServletRequest request) {
		Map<String, Object> result = problemService.createProblemCategory(
				problemCategorySlug, problemCategoryName, parentProblemCategorySlug);
		
		if ( (boolean) result.get("isSuccessful") ) {
			long problemCategoryId = (Long) result.get("problemCategoryId");
			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			
			LOGGER.info(String.format("ProblemCategory: [ProblemCategoryId=%s] was created by administrator at %s.", 
					new Object[] {problemCategoryId, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 编辑试题分类.
	 * @param problemCategoryId - 试题分类的唯一标识符
	 * @param problemCategorySlug - 试题分类的别名
	 * @param problemCategoryName - 试题分类的名称
	 * @param parentProblemCategorySlug - 父级试题分类的别名
	 * @param request - HttpServletRequest对象
	 * @return 包含试题分类的编辑结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "编辑试题分类", notes = "根据结果中的isSuccessful判断是否成功")
	@RequestMapping(value="/editProblemCategory.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData editProblemCategoryAction(
			@ApiParam(value="试题分类的唯一标识符", name="problemCategoryId")
			@RequestParam(value="problemCategoryId") String problemCategoryId,
			@ApiParam(value="试题分类的别名", name="problemCategorySlug")
			@RequestParam(value="problemCategorySlug") String problemCategorySlug,
			@ApiParam(value="试题分类的名称", name="problemCategoryName")
			@RequestParam(value="problemCategoryName") String problemCategoryName,
			@ApiParam(value="父级试题分类的别名", name="parentProblemCategory")
			@RequestParam(value="parentProblemCategory") String parentProblemCategorySlug,
			HttpServletRequest request) {
		Map<String, Boolean> result = problemService.editProblemCategory(
				Integer.parseInt(problemCategoryId), problemCategorySlug, 
				problemCategoryName, parentProblemCategorySlug);

		if ( result.get("isSuccessful") ) {
			String ipAddress = HttpRequestParser.getRemoteAddr(request);

			LOGGER.info(String.format("ProblemCategory: [ProblemCategoryId=%s] was edited by administrator at %s.",
					new Object[] {problemCategoryId, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 删除试题分类.
	 * @param problemCategories - 试题分类的唯一标识符集合
	 * @param request - HttpServletRequest对象
	 * @return 包含试题分类的删除结果的Map<String, Boolean>对象
	 */
	@ApiOperation(value = "删除试题分类")
	@RequestMapping(value="/deleteProblemCategories.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData deleteProblemCategoryAction(
			@ApiParam(value="试题分类的唯一标识符集合", name="problemCategories")
			@RequestParam(value="problemCategories") String problemCategories,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>(3, 1);
		List<Integer> problemCategoryList = JSON.parseArray(problemCategories, Integer.class);
		List<Integer> deletedProblemCategories = new ArrayList<>();

		for ( int problemCategoryId : problemCategoryList ) {
			if ( problemService.deleteProblemCategory(problemCategoryId) ) {
				deletedProblemCategories.add(problemCategoryId);
			}
			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			LOGGER.info(String.format("ProblemCategory: [ProblemCategoryId=%s] was deleted by administrator at %s.",
					new Object[] {problemCategoryId, ipAddress}));
		}
		result.put("isSuccessful", true);
		result.put("deletedProblemCategories", deletedProblemCategories);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 加载提交列表页面.
	 * @param problemId - 提交对应试题的唯一标识符
	 * @param username - 提交者的用户名
	 * @param pageNumber - 当前页面的页码
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含提交列表页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载提交列表页面")
	@RequestMapping(value="/all-submissions", method=RequestMethod.GET)
	public ResponseData allSubmissionsView(
			@ApiParam(value="提交对应试题的唯一标识符", name="problemId", required=false, defaultValue="0")
			@RequestParam(value="problemId", required=false, defaultValue="0") long problemId,
			@ApiParam(value="提交者的用户名", name="username", required=false, defaultValue="")
			@RequestParam(value="username", required=false, defaultValue="") String username,
			@ApiParam(value="当前页面的页码", name="page", required=false, defaultValue="1")
			@RequestParam(value="page", required=false, defaultValue="1") long pageNumber,
			HttpServletRequest request, HttpServletResponse response) {
		final int NUMBER_OF_SUBMISSIONS_PER_PAGE = 100;
		
		long totalSubmissions = submissionService.getNumberOfSubmissionsUsingProblemIdAndUsername(problemId, username);
		long latestSubmissionId = submissionService.getLatestSubmissionId();
		long offset = latestSubmissionId - (pageNumber >= 1 ? pageNumber - 1 : 0) * NUMBER_OF_SUBMISSIONS_PER_PAGE;
		List<Submission> submissions = submissionService.getSubmissions(problemId, username, offset, NUMBER_OF_SUBMISSIONS_PER_PAGE);
		
//		ModelAndView view = new ModelAndView("administration/all-submissions");
//		view.addObject("problemId", problemId);
//		view.addObject("username", username);
//		view.addObject("currentPage", pageNumber);
//		view.addObject("totalPages", (long) Math.ceil(totalSubmissions * 1.0 / NUMBER_OF_SUBMISSIONS_PER_PAGE));
//		view.addObject("submissions", submissions);

		Map<String, Object> result = new HashMap<>();
		result.put("problemId", problemId);
		result.put("username", username);
		result.put("currentPage", pageNumber);
		result.put("totalPages", (long) Math.ceil(totalSubmissions * 1.0 / NUMBER_OF_SUBMISSIONS_PER_PAGE));
		result.put("submissions", submissions);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 删除选定的提交记录.
	 * @param submissions - 提交记录ID的集合, 以逗号(, )分隔
	 * @param request - HttpServletRequest对象
	 * @return 提交记录的删除结果
	 */
	@ApiOperation(value = "删除选定的提交记录")
	@RequestMapping(value="/deleteSubmissions.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData deleteSubmissionsAction(
			@ApiParam(value="提交记录ID的集合, 以逗号(, )分隔", name="submissions")
			@RequestParam(value="submissions") String submissions,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>(3, 1);
		List<Long> submissionList = JSON.parseArray(submissions, Long.class);
		List<Long> deletedSubmissions = new ArrayList<>();

		for ( Long submissionId : submissionList ) {
			if ( submissionService.deleteSubmission(submissionId) ) {
				deletedSubmissions.add(submissionId);
			}
			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			LOGGER.info(String.format("Submission: [SubmissionId=%s] deleted by administrator at %s.", 
					new Object[] {submissionId, ipAddress}));
		}
		result.put("isSuccessful", true);
		result.put("deletedSubmissions", deletedSubmissions);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 重新评测选定的提交记录.
	 * @param submissions - 提交记录ID的集合, 以逗号(, )分隔
	 * @param request - HttpServletRequest对象
	 * @return 重新评测请求的执行结果
	 */
	@ApiOperation(value = "重新评测选定的提交记录")
	@RequestMapping(value="/restartSubmissions.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData restartSubmissionsAction(
			@RequestParam(value="submissions") String submissions,
			HttpServletRequest request) {
		Map<String, Boolean> result = new HashMap<>(2, 1);
		List<Long> submissionList = JSON.parseArray(submissions, Long.class);
		
		for ( Long submissionId : submissionList ) {
			submissionService.createSubmissionTask(submissionId);
		}
		result.put("isSuccessful", true);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 查看提交记录.
	 * @param submissionId - 提交记录的唯一标识符
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含提交记录信息的ModelAndView对象
	 */
	@ApiOperation(value = "查看提交记录")
	@RequestMapping(value="/edit-submission/{submissionId}", method=RequestMethod.GET)
	public ResponseData editSubmissionView(
			@PathVariable(value = "submissionId") long submissionId,
			HttpServletRequest request, HttpServletResponse response) {
		Submission submission = submissionService.getSubmission(submissionId);
		if ( submission == null ) {
			throw new ResourceNotFoundException();
		}
//		ModelAndView view = new ModelAndView("administration/edit-submission");
//		view.addObject("submission", submission);
//		view.addObject("csrfToken", CsrfProtector.getCsrfToken(request.getSession()));
		Map<String, Object> result = new HashMap<>();
		result.put("submission", submission);
		result.put("csrfToken", CsrfProtector.getCsrfToken(request.getSession()));
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载竞赛列表页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含参赛人数列表页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载竞赛列表页面")
	@RequestMapping(value="/all-contests", method=RequestMethod.GET)
	public ResponseData allContestsView(
			@ApiParam(value="关键字", name="keyword", required=false, defaultValue="")
			@RequestParam(value="keyword", required=false, defaultValue="") String keyword,
			@ApiParam(value="当前页面的页码", name="page", required=false, defaultValue="1")
			@RequestParam(value="page", required=false, defaultValue="1") long pageNumber,
			HttpServletRequest request, HttpServletResponse response) {
		final int NUMBER_OF_CONTESTS_PER_PAGE = 10;
		long totalContests = contestService.getNumberOfContests(keyword);
		List<Contest> contests = contestService.getContests(keyword, 0, (int) totalContests);
//		ModelAndView view = new ModelAndView("administration/all-contests");
//		view.addObject("currentTime", new Date());
//		view.addObject("keyword", keyword);
//		view.addObject("currentPage", pageNumber);
//		view.addObject("totalPages", (long) Math.ceil(totalContests * 1.0 / NUMBER_OF_CONTESTS_PER_PAGE));
//		view.addObject("contests", contests);

		Map<String, Object> result = new HashMap<>();
		result.put("currentTime", new Date());
		result.put("keyword", keyword);
		result.put("currentPage", pageNumber);
		result.put("totalPages", (long) Math.ceil(totalContests * 1.0 / NUMBER_OF_CONTESTS_PER_PAGE));
		result.put("contests", contests);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 删除选定的竞赛.
	 * @param contests - 试题ID的集合, 以逗号(, )分隔
	 * @param request - HttpServletRequest对象
	 * @return 试题的删除结果
	 */
	@ApiOperation(value = "删除选定的竞赛")
	@RequestMapping(value="/deleteContests.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData deleteContestsAction(
			@ApiParam(value="试题ID的集合, 以逗号(, )分隔", name="contests")
			@RequestParam(value="contests") String contests,
			HttpServletRequest request) {
		Map<String, Boolean> result = new HashMap<>(2, 1);
		List<Long> contestList = JSON.parseArray(contests, Long.class);

		for ( Long contestId : contestList ) {
			contestService.deleteContest(contestId);

			String ipAddress = HttpRequestParser.getRemoteAddr(request);
			LOGGER.info(String.format("Contest: [ContestId=%s] was deleted by administrator at %s.",
					new Object[] {contestId, ipAddress}));
		}
		result.put("isSuccessful", true);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 加载创建竞赛页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含创建竞赛页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "")
	@RequestMapping(value="/new-contest", method=RequestMethod.GET)
	public ModelAndView newContestView(
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("administration/new-contest");
	}

//	/**
//	 * 处理用户创建竞赛的请求.
//	 * @param ContestName - 试题名称
//	 * @param StartTime - 时间限制
//	 * @param EndTime - 内存占用限制
//	 * @param description - 试题描述
//	 * @param ContestMode - 试题提示
//	 * @param ChooseProblem - 输入格式
//	 */
//	@RequestMapping(value="/createProblem.action", method=RequestMethod.POST)
//	public @ResponseBody Map<String, Object> createContestAction(
//			@RequestParam(value="CroblemName") String ContestName,
//			@RequestParam(value="StartTime") String StartTime,
//			@RequestParam(value="EndTime") String EndTime,
//			@RequestParam(value="description") String description,
//			@RequestParam(value="ContestMode") String ContestMode,
//			@RequestParam(value="ChooseProblem") String ChooseProblem,
//			HttpServletRequest request) {
//		Map<String, Object> result = contestService.createContest(ContestName,description,StartTime,EndTime,ContestMode,ChooseProblem);
//		if ( (boolean) result.get("isSuccessful") ) {
//			long problemId = (Long) result.get("problemId");
//			String ipAddress = HttpRequestParser.getRemoteAddr(request);
//
//			LOGGER.info(String.format("Contest: [ProblemId=%s] was created by administrator at %s.",
//					new Object[] {problemId, ipAddress}));
//		}
//		return result;
//	}

	/**
	 * 加载常规选项页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含常规选项页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载常规选项页面")
	@RequestMapping(value="/general-settings", method=RequestMethod.GET)
	public ResponseData generalSettingsView(
			HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView view = new ModelAndView("administration/general-settings");
//		view.addObject("options", getOptions());
		Map<String, Object> result = new HashMap<>();
		result.put("options", getOptions());
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 获取系统全部的选项, 以键值对的形式返回.
	 * @return 键值对形式的系统选项
	 */
	@ApiOperation(value = "获取系统全部的选项, 以键值对的形式返回")
	private ResponseData getOptions() {
		Map<String, String> optionMap = new HashMap<>();
		List<Option> options = optionService.getOptions();
		
		for ( Option option : options ) {
			optionMap.put(option.getOptionName(), option.getOptionValue());
		}
		return ResponseData.ok().data("optionMap",optionMap);
	}
	
	/**
	 * 更新网站常规选项.
	 * @param websiteName - 网站名称
	 * @param websiteDescription - 网站描述
	 * @param copyright - 网站版权信息
	 * @param allowUserRegister - 是否允许用户注册
	 * @param icpNumber - 网站备案号
	 * @param policeIcpNumber - 公安备案号
	 * @param googleAnalyticsCode - Google Analytics代码
	 * @param offensiveWords - 敏感词列表
	 * @param request - HttpServletRequest对象
	 * @return 网站常规选项的更新结果
	 */
	@ApiOperation(value = "更新网站常规选项")
	@RequestMapping(value="/updateGeneralSettings.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData updateGeneralSettingsAction(
			@ApiParam(value="网站名称", name="websiteName")
			@RequestParam(value="websiteName") String websiteName,
			@ApiParam(value="网站描述", name="websiteDescription")
			@RequestParam(value="websiteDescription") String websiteDescription,
			@ApiParam(value="网站版权信息", name="copyright")
			@RequestParam(value="copyright") String copyright,
			@ApiParam(value="是否允许用户注册", name="allowUserRegister")
			@RequestParam(value="allowUserRegister") boolean allowUserRegister,
			@ApiParam(value="网站备案号", name="icpNumber")
			@RequestParam(value="icpNumber") String icpNumber,
			@ApiParam(value="公安备案号", name="policeIcpNumber")
			@RequestParam(value="policeIcpNumber") String policeIcpNumber,
			@ApiParam(value="Google Analytics代码", name="googleAnalyticsCode")
			@RequestParam(value="googleAnalyticsCode") String googleAnalyticsCode,
			@ApiParam(value="敏感词列表", name="offensiveWords")
			@RequestParam(value="offensiveWords") String offensiveWords,
			HttpServletRequest request) {
		Map<String, Boolean> result = optionService.updateOptions(websiteName, websiteDescription, 
				copyright, allowUserRegister, icpNumber, policeIcpNumber, googleAnalyticsCode, offensiveWords);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 加载编程语言设置页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含编程语言设置信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载编程语言设置页面")
	@RequestMapping(value="/language-settings", method=RequestMethod.GET)
	public ResponseData languageSettingsView(
			HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView view = new ModelAndView("administration/language-settings");
//		view.addObject("languages", languageService.getAllLanguages());
		Map<String, Object> result = new HashMap<>();
		result.put("languages", languageService.getAllLanguages());
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 更新网站编程语言选项.
	 * @param languages - 包含编程语言设置的数组
	 * @param request - HttpServletRequest对象
	 * @return 编程语言选项的更新结果
	 */
	@ApiOperation(value = "更新网站编程语言选项")
	@RequestMapping(value="/updateLanguageSettings.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData updateLanguageSettingsAction(
			@ApiParam(value="包含编程语言设置的数组", name="languages")
			@RequestParam(value="languages") String languages,
			HttpServletRequest request) {
		List<Language> languagesList = JSON.parseArray(languages, Language.class);
		Map<String, Object> result = languageService.updateLanguageSettings(languagesList);
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 自动注入的UserService对象.
	 */
	@Autowired
	private UserService userService;
	
	/**
	 * 自动注入的ProblemService对象.
	 * 用于获取试题记录信息.
	 */
	@Autowired
	private ProblemService problemService;

	/**
	 * 自动注入的FpsProblemService对象.
	 * 用于获取试题记录信息.
	 */
	@Autowired
	private FpsProblemService fpsProblemService;
	
	/**
	 * 自动注入的SubmissionService对象.
	 * 用于获取提交记录信息.
	 */
	@Autowired
	private SubmissionService submissionService;

	/**
	 * 自动注入的OptionService对象.
	 * 用于获取系统中的设置选项.
	 */
	@Autowired
	private OptionService optionService;
	
	/**
	 * 自动注入的LanguageService对象.
	 * 用于获取系统中的编程语言选项.
	 */
	@Autowired
	private LanguageService languageService;

	/**
	 * 自动注入的ContestService对象
	 * 用于获取竞赛相关的信息
	 */
	@Autowired
	private ContestService contestService;
	
	/**
	 * 自动注入的ApplicationEventListener对象.
	 * 用于获取在线评测机的数量.
	 */
	@Autowired
	private ApplicationEventListener eventListener;
	
	/**
	 * 日志记录器.
	 */
	private static final Logger LOGGER = LogManager.getLogger(org.verwandlung.voj.web.service.AdministrationClientService.class);
}
