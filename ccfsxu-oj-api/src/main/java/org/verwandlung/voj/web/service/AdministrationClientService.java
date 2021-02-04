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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用于处理系统管理的请求.
 *
 */
@FeignClient(value = "CCUSXU-OJ")
@Api(tags = "用于处理系统管理的请求.")
@RequestMapping(value="/administration")
public interface AdministrationClientService {
	/**
	 * 加载系统管理首页.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含系统管理页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载系统管理首页", notes = "先登陆管理员账号")
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseData indexView(
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request);
	
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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);
	
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
			@RequestParam(value="request") HttpServletRequest request);

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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request);

	/**
	 * 加载创建用户页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含创建用户页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载创建用户页面")
	@RequestMapping(value="/new-user", method=RequestMethod.GET)
	public ResponseData newUserView(
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request);

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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request);

	/**
	 * 加载创建试题页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含创建试题页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载创建试题页面")
	@RequestMapping(value="/new-problem", method=RequestMethod.GET)
	public ResponseData newProblemView(
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request);

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
			@RequestParam(value="request") HttpServletRequest request);

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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);
	
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
			@RequestParam(value="request") HttpServletRequest request);
	
	/**
	 * 加载试题分类页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含试题分类页面信息的ModelAndView对象.
	 */
	@ApiOperation(value = "加载试题分类页面")
	@RequestMapping(value="/problem-categories", method=RequestMethod.GET)
	public ResponseData problemCategoriesView(
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);
	
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
			@RequestParam(value="request") HttpServletRequest request);
	
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
			@RequestParam(value="request") HttpServletRequest request);

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
			@RequestParam(value="request") HttpServletRequest request);
	
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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);
	
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
			@RequestParam(value="request") HttpServletRequest request);
	
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
			@RequestParam(value="request") HttpServletRequest request);
	
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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
			@RequestParam(value="request") HttpServletRequest request);

	/**
	 * 加载创建竞赛页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含创建竞赛页面信息的ModelAndView对象
	 */
	@ApiOperation(value = "")
	@RequestMapping(value="/new-contest", method=RequestMethod.GET)
	public ModelAndView newContestView(
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);

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
//			@RequestParam(value="request") HttpServletRequest request) {
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
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);
	
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
			@RequestParam(value="request") HttpServletRequest request);
	
	/**
	 * 加载编程语言设置页面.
	 * @param request - HttpServletRequest对象
	 * @param response - HttpServletResponse对象
	 * @return 包含编程语言设置信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载编程语言设置页面")
	@RequestMapping(value="/language-settings", method=RequestMethod.GET)
	public ResponseData languageSettingsView(
			@RequestParam(value="request") HttpServletRequest request, @RequestParam(value="response") HttpServletResponse response);
	
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
			@RequestParam(value="request") HttpServletRequest request);

}
