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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.verwandlung.voj.web.exception.ResourceNotFoundException;
import org.verwandlung.voj.web.model.*;
import org.verwandlung.voj.web.service.ContestService;
import org.verwandlung.voj.web.service.LanguageService;
import org.verwandlung.voj.web.service.ProblemService;
import org.verwandlung.voj.web.service.SubmissionService;
import org.verwandlung.voj.web.util.CsrfProtector;
import org.verwandlung.voj.web.util.HttpRequestParser;
import org.verwandlung.voj.web.util.HttpSessionParser;
import org.verwandlung.voj.web.util.ResponseData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理竞赛的相关请求.
 *
 */
@Api(tags = "处理竞赛的相关请求")
@RestController
@RequestMapping(value="/contest")
public class ContestsController {
	/**
	 * 显示竞赛列表页面.
	 * @param keyword - 竞赛的关键词
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含竞赛列表页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示竞赛列表页面")
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseData contestsView(
			@ApiParam(value="关键字", name="keyword", required = false)
			@RequestParam(value="keyword", required = false) String keyword,
			HttpServletRequest request, HttpServletResponse response) {
		List<Contest> contests = contestService.getContests(keyword, 0, NUMBER_OF_CONTESTS_PER_PAGE);

//		ModelAndView view = new ModelAndView("contests/contests");
//		view.addObject("contests", contests);
//		view.addObject("currentTime", new Date());
		Map<String, Object> result = new HashMap<>();
		result.put("contests", contests);
		result.put("currentTime", new Date());
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 获取竞赛的列表.
	 * @param keyword - 竞赛的关键词
	 * @param startIndex - 当前加载的最后一条记录的索引值 (Index)
	 * @param request - HttpRequest对象
	 * @return 一个包含竞赛列表的HashMap对象
	 */
	@ApiOperation(value = "获取竞赛的列表")
	@RequestMapping(value="/getContests.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getContestsAction(
			@ApiParam(value="关键字",name="keyword")
			@RequestParam(value="keyword", required=false) String keyword,
			@ApiParam(value="当前加载的最后一条记录的索引值 (Index)",name="startIndex")
			@RequestParam(value="startIndex") long startIndex,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>(3, 1);

		List<Contest> contests = contestService.getContests(keyword, startIndex, NUMBER_OF_CONTESTS_PER_PAGE);
		result.put("isSuccessful", contests != null && !contests.isEmpty());
		result.put("contests", contests);

		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 显示竞赛详细信息的页面.
	 * @param contestId - 竞赛的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含提交详细信息的ModelAndView对象 
	 */
	@ApiOperation(value = "显示竞赛详细信息的页面")
	@RequestMapping(value="/{contestId}", method=RequestMethod.GET)
	public ResponseData contestView(
			@ApiParam(value="竞赛的唯一标识符",name="contestId")
			@PathVariable("contestId") long contestId,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User currentUser = HttpSessionParser.getCurrentUser(session);
		Contest contest = contestService.getContest(contestId);
		if ( contest == null ) {
			throw new ResourceNotFoundException();
		}

		boolean isAttended = contestService.isAttendContest(contestId, currentUser);
		long numberOfContestants = contestService.getNumberOfContestantsOfContest(contestId);
		List<Long> problemIdList = JSON.parseArray(contest.getProblems(), Long.class);
		List<Problem> problems = contestService.getProblemsOfContests(problemIdList);
		Map<Long, ContestSubmission> submissions = contestService.getSubmissionsOfContestantOfContest(contestId, currentUser);

//		ModelAndView view = new ModelAndView("contests/contest");	//views/contest/contest.jsp
//		view.addObject("currentTime", new Date())
//			.addObject("contest", contest)
//			.addObject("problems", problems)
//			.addObject("submissions", submissions)
//			.addObject("isAttended", isAttended)
//			.addObject("numberOfContestants", numberOfContestants)
//			.addObject("csrfToken", CsrfProtector.getCsrfToken(request.getSession()));
		Map<String, Object> result = new HashMap<>();
		result.put("currentTime", new Date());
		result.put("contest", contest);
		result.put("problems", problems);
		result.put("submissions", submissions);
		result.put("isAttended", isAttended);
		result.put("numberOfContestants", numberOfContestants);
		result.put("csrfToken", CsrfProtector.getCsrfToken(request.getSession()));;
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 处理用户参加竞赛的请求.
	 * @param contestId - 竞赛的唯一标识符
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含是否成功参加竞赛状态信息的Map对象
	 */
	@ApiOperation(value = "处理用户参加竞赛的请求")
	@RequestMapping(value="/{contestId}/attend.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData attendContestAction(
			@ApiParam(value="竞赛的唯一标识符",name="contestId")
			@PathVariable("contestId") long contestId,
			@ApiParam(value="用于防止CSRF攻击的Token", name="csrfToken")
			@RequestParam(value="csrfToken") String csrfToken,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		User currentUser = HttpSessionParser.getCurrentUser(session);
		boolean isCsrfTokenValid = CsrfProtector.isCsrfTokenValid(csrfToken, session);

		Map<String, Boolean> result = contestService.attendContest(contestId, currentUser, isCsrfTokenValid);
		if ( result.get("isSuccessful") ) {
			LOGGER.info(String.format("User: {%s} attended contest #%d at %s",
					new Object[] {currentUser, contestId, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 显示排行榜.
	 * @param contestId - 竞赛的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含竞赛排行榜的ModelAndView对象
	 */
	@ApiOperation(value = "显示排行榜")
	@RequestMapping(value="/{contestId}/leader_board", method=RequestMethod.GET)
	public ResponseData leaderBoardView(
			@ApiParam(value="竞赛的唯一标识符",name="contestId")
			@PathVariable("contestId") long contestId,
			HttpServletRequest request, HttpServletResponse response) {
		Contest contest = contestService.getContest(contestId);
		Date currentTime = new Date();
		if ( contest == null || contest.getStartTime().after(currentTime) ||
				!(contest.getContestMode().equals("OI") || contest.getContestMode().equals("ACM")) ) {
			throw new ResourceNotFoundException();
		}

		List<Long> problemIdList = JSON.parseArray(contest.getProblems(), Long.class);
		List<Problem> problems = contestService.getProblemsOfContests(problemIdList);
		ModelAndView view = null;
		Map<String, Object> ContestResult;
		Map<String, Object> result = new HashMap<>();

		if ( contest.getContestMode().equals("OI") ) {
			result.put("ContestMode","OI");
			ContestResult = contestService.getLeaderBoardForOi(contestId);
		} else if ( contest.getContestMode().equals("ACM") ) {
			result.put("ContestMode","ACM");
			ContestResult = contestService.getLeaderBoardForAcm(contestId);
		} else {
			throw new ResourceNotFoundException();
		}
		List<ContestContestant> contestants = (List<ContestContestant>) ContestResult.get("contestants");
		Map<Long, Map<Long, Submission>> submissions = (Map<Long, Map<Long, Submission>>) ContestResult.get("submissions");
//		view.addObject("contestants", contestants);
//		view.addObject("submissions", submissions);
//		view.addObject("contest", contest);
//		view.addObject("problems", problems);
		result.put("contestants", contestants);
		result.put("submissions", submissions);
		result.put("contest", contest);
		result.put("problems", problems);
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 显示竞赛中的试题信息.
	 * @param contestId - 竞赛的唯一标识符
	 * @param problemId - 试题的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含竞赛试题信息的ModelAndView对象
	 */
	@ApiOperation(value = "显示竞赛中的试题信息")
	@RequestMapping(value="/{contestId}/p/{problemId}", method=RequestMethod.GET)
	public ResponseData problemView(
			@ApiParam(value="竞赛的唯一标识符", name="contestId")
			@PathVariable("contestId") long contestId,
			@ApiParam(value="试题的唯一标识符", name="problemId")
			@PathVariable("problemId") long problemId,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User currentUser = HttpSessionParser.getCurrentUser(session);
		Contest contest = contestService.getContest(contestId);
		if ( contest == null ) {
			throw new ResourceNotFoundException();
		}
		// 试题不存在于竞赛试题列表中
		List<Long> problems = JSON.parseArray(contest.getProblems(), Long.class);
		if ( !problems.contains(problemId) ) {
			throw new ResourceNotFoundException();
		}
		// 未参加竞赛者在竞赛结束前无法查看试题
		Date currentTime = new Date();
		boolean isAttended = contestService.isAttendContest(contestId, currentUser);
		if ( contest.getEndTime().after(currentTime) && !isAttended ) {
			throw new ResourceNotFoundException();
		}

		Problem problem = problemService.getProblem(problemId);
		List<Language> languages = languageService.getAllLanguages();
		Map<String, String> codeSnippet = contestService.getCodeSnippetOfContestProblem(contest, problemId, currentUser);
		List<Submission> submissions = contestService.getSubmissionsOfContestantOfContestProblem(contest, problemId, currentUser);
//		ModelAndView view = new ModelAndView("problems/problem");
//		view.addObject("contest", contest);
//		view.addObject("problem", problem);
//		view.addObject("languages", languages);
//		view.addObject("codeSnippet", codeSnippet);
//		view.addObject("submissions", submissions);
//		view.addObject("currentTime", currentTime);
//		view.addObject("isContest", true);
//		view.addObject("csrfToken", CsrfProtector.getCsrfToken(session));
		Map<String, Object> result = new HashMap<>();
		result.put("contest", contest);
		result.put("problem", problem);
		result.put("languages", languages);
		result.put("codeSnippet", codeSnippet);
		result.put("submissions", submissions);
		result.put("currentTime", currentTime);
		result.put("isContest", true);
		result.put("csrfToken", CsrfProtector.getCsrfToken(session));
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 创建提交.
	 * @param problemId - 试题的唯一标识符
	 * @param languageSlug - 编程语言的别名
	 * @param code - 代码
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpRequest对象
	 * @return 一个包含提交记录创建结果的Map<String, Object>对象
	 */
	@ApiOperation(value = "创建提交")
	@RequestMapping(value="/{contestId}/createSubmission.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createSubmissionAction(
			@ApiParam(value = "试题的唯一标识符", name = "problemId")
			@RequestParam(value = "problemId") long problemId,
			@ApiParam(value = "编程语言的别名", name = "languageSlug")
			@RequestParam(value = "languageSlug") String languageSlug,
			@ApiParam(value = "代码", name = "code")
			@RequestParam(value = "code") String code,
			@ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
			@RequestParam(value = "csrfToken") String csrfToken,
			HttpServletRequest request, @PathVariable String contestId) {
		HttpSession session = request.getSession();
		System.out.println("ContestController session = "+session);
		String ipAddress = HttpRequestParser.getRemoteAddr(request);
		User currentUser = HttpSessionParser.getCurrentUser(session);
		boolean isCsrfTokenValid = CsrfProtector.isCsrfTokenValid(csrfToken, session);

		Map<String, Object> result = submissionService.createSubmission(
				currentUser, problemId, languageSlug, code, isCsrfTokenValid);
		Contest contest = contestService.getContest(Long.parseLong(contestId));
		LOGGER.debug(result.toString());
		Submission submission = submissionService.getSubmission((Long)result.get("submissionId"));
		boolean isCreateContestSubmission = contestService.createContestSubmission(contest,submission);
		boolean isSuccessful = (Boolean)result.get("isSuccessful");
		if ( isSuccessful && isCreateContestSubmission) {
			long submissionId = (Long)result.get("submissionId");
			LOGGER.info(String.format("User: {%s} submitted code with SubmissionId #%s at %s",
					new Object[] {currentUser, submissionId, ipAddress}));
		}
		return ResponseData.ok().data("result",result);
	}

	/**
	 * 每次查询需要加载竞赛的数量.
	 */
	private static final int NUMBER_OF_CONTESTS_PER_PAGE = 10;

	/**
	 * 自动注入的ContestService对象.
	 */
	@Autowired
	private ContestService contestService;

	/**
	 * 自动注入的ProblemService对象.
	 * 用于查询试题信息.
	 */
	@Autowired
	private ProblemService problemService;

	/**
	 * 自动注入的LanguageService对象.
	 * 用于加载试题详情页的语言选项.
	 */
	@Autowired
	private LanguageService languageService;

	/**
	 * 自动注入的SubmissionService对象.
	 * 用于创建提交记录.
	 */
	@Autowired
	private SubmissionService submissionService;

	/**
	 * 日志记录器.
	 */
	private static final Logger LOGGER = LogManager.getLogger(org.verwandlung.voj.web.service.ContestsClientService.class);
}
