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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.verwandlung.voj.web.service.DiscussionClientService;
import org.verwandlung.voj.web.util.ResponseData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理讨论的相关请求.
 *
 */
@Api(tags = "处理讨论的相关请求")
@RestController
@RequestMapping(value="/consumer/discussion")
public class DiscussionController_Consumer {
	/**
	 * 显示讨论列表页面.
	 * @param discussionTopicSlug - 讨论话题的唯一英文缩写
	 * @param problemId - 试题的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含讨论列表页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示讨论列表页面")
	@RequestMapping(value="", method= RequestMethod.GET)
	public ResponseData discussionThreadsView(
            @ApiParam(value = "讨论话题的唯一英文缩写", name = "topicSlug", required = false, defaultValue = "")
            @RequestParam(value = "topicSlug", required = false, defaultValue = "") String discussionTopicSlug,
            @ApiParam(value = "试题的唯一标识符", name = "problemId", required = false, defaultValue = "-1")
            @RequestParam(value = "problemId", required = false, defaultValue = "-1") long problemId,
            HttpServletRequest request, HttpServletResponse response){
		return this.discussionClientService
				.discussionThreadsView(discussionTopicSlug, problemId, request, response);
	}

	/**
	 * 获取讨论帖子列表.
	 * @param startIndex - 获取讨论帖子的Offset
	 * @param discussionTopicSlug - 讨论话题的唯一英文缩写
	 * @param problemId - 试题的唯一标识符
	 * @return 一个包含讨论帖子列表的HashMap对象
	 */
	@ApiOperation(value = "获取讨论帖子列表")
	@RequestMapping(value="/getDiscussionThreads.action", method=RequestMethod.GET)
	public @ResponseBody
	ResponseData getDiscussionThreadsAction(
            @ApiParam(value = "获取讨论帖子的Offset", name = "startIndex")
            @RequestParam(value = "startIndex") long startIndex,
            @ApiParam(value = "讨论话题的唯一英文缩写", name = "topicSlug")
            @RequestParam(value = "topicSlug", required = false, defaultValue = "") String discussionTopicSlug,
            @ApiParam(value = "试题的唯一标识符", name = "problemId")
            @RequestParam(value = "problemId", required = false, defaultValue = "-1") long problemId,
            HttpServletRequest request){
		return this.discussionClientService
				.getDiscussionThreadsAction(startIndex, discussionTopicSlug, problemId, request);
	}

	/**
	 * 显示讨论详情页面.
	 * @param discussionThreadId - 讨论帖子的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含讨论详情页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示讨论详情页面")
	@RequestMapping(value="/{threadId}", method=RequestMethod.GET)
	public ResponseData discussionThreadView(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "threadId")
            @PathVariable("threadId") long discussionThreadId,
            HttpServletRequest request, HttpServletResponse response){
		return this.discussionClientService
				.discussionThreadView(discussionThreadId, request, response);
	}

	/**
	 * 显示创建讨论页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含创建讨论页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示创建讨论页面")
	@RequestMapping(value="/new", method=RequestMethod.GET)
	public ResponseData newDiscussionThreadView(
            @ApiParam(value = "试题的唯一编号", name = "problemId", required = false, defaultValue = "-1")
            @RequestParam(value = "problemId", required = false, defaultValue = "-1") long problemId,
            HttpServletRequest request, HttpServletResponse response){
		return this.discussionClientService
				.newDiscussionThreadView(problemId, request, response);
	}

	/**
	 * 获取讨论回复.
	 * @param discussionThreadId - 讨论帖子的唯一标识符
	 * @param startIndex - 讨论回复的起始Offset(已经获取的回复的数量).
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论回复列表(DiscussionReply)的Map对象
	 */
	@ApiOperation(value = "获取讨论回复")
	@RequestMapping(value="/{threadId}/getDiscussionReplies.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getDiscussionRepliesAction(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "threadId")
            @PathVariable("threadId") long discussionThreadId,
            @ApiParam(value = "讨论回复的起始Offset(已经获取的回复的数量).", name = "startIndex")
            @RequestParam(value = "startIndex") long startIndex,
            HttpServletRequest request){
		return this.discussionClientService
				.getDiscussionRepliesAction(discussionThreadId, startIndex, request);
	}

	/**
	 * 处理用户对讨论回复投票的请求.
	 * @param discussionThreadId - 讨论帖子的唯一标识符
	 * @param discussionReplyId - 讨论回复的唯一标识符
	 * @param voteUp - Vote Up状态 (+1 表示用户赞了这个回答, -1 表示用户取消赞了这个回答, 0表示没有操作)
	 * @param voteDown - Vote Down状态 (+1 表示用户踩了这个回答, -1 表示用户取消踩了这个回答, 0表示没有操作)
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论回复投票请求处理结果的JSON对象
	 */
	@ApiOperation(value = "处理用户对讨论回复投票的请求")
	@RequestMapping(value="/{threadId}/voteDiscussionReply.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData voteDiscussionReplyAction(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "threadId")
            @PathVariable("threadId") long discussionThreadId,
            @ApiParam(value = "讨论回复的唯一标识符", name = "discussionReplyId")
            @RequestParam(value = "discussionReplyId") long discussionReplyId,
            @ApiParam(value = "Vote Up状态 (+1 表示用户赞了这个回答, -1 表示用户取消赞了这个回答, 0表示没有操作)", name = "voteUp")
            @RequestParam(value = "voteUp") int voteUp,
            @ApiParam(value = "Vote Down状态 (+1 表示用户踩了这个回答, -1 表示用户取消踩了这个回答, 0表示没有操作)", name = "voteDown")
            @RequestParam(value = "voteDown") int voteDown,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.discussionClientService
				.voteDiscussionReplyAction(discussionThreadId, discussionReplyId, voteUp, voteDown, csrfToken, request);
	}

	/**
	 * 处理用户创建讨论帖子的请求.
	 * @param discussionTopicSlug - 讨论帖子对应主题的唯一英文缩写
	 * @param relatedProblemIdString - 讨论帖子所关联问题的唯一标识符
	 * @param discussionThreadTitle - 讨论帖子的标题
	 * @param csrfToken 用于防止CSRF攻击的Token
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论帖子创建结果的JSON对象
	 */
	@ApiOperation(value = "处理用户创建讨论帖子的请求")
	@RequestMapping(value="/createDiscussionThread.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createDiscussionThreadAction(
            @ApiParam(value = "讨论帖子对应主题的唯一英文缩写", name = "discussionTopicSlug")
            @RequestParam(value = "discussionTopicSlug") String discussionTopicSlug,
            @ApiParam(value = "讨论帖子所关联问题的唯一标识符", name = "relatedProblemId")
            @RequestParam(value = "relatedProblemId") String relatedProblemIdString,
            @ApiParam(value = "讨论帖子的标题", name = "threadTitle")
            @RequestParam(value = "threadTitle") String discussionThreadTitle,
            @ApiParam(value = "讨论帖子的内容", name = "threadContent")
            @RequestParam(value = "threadContent") String discussionThreadContent,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.discussionClientService
				.createDiscussionThreadAction(discussionTopicSlug, relatedProblemIdString, discussionThreadTitle, discussionThreadContent, csrfToken, request);
	}

	/**
	 * 处理用户编辑讨论帖子的请求.
	 * @param discussionThreadId - 讨论帖子的唯一标识符
	 * @param discussionTopicSlug - 讨论帖子对应主题的唯一英文缩写
	 * @param discussionThreadTitle - 讨论帖子的标题
	 * @param csrfToken 用于防止CSRF攻击的Token
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论帖子编辑结果的JSON对象
	 */
	@ApiOperation(value = "处理用户编辑讨论帖子的请求")
	@RequestMapping(value="/editDiscussionThread.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData editDiscussionThreadAction(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "discussionThreadId")
            @RequestParam(value = "discussionThreadId") long discussionThreadId,
            @ApiParam(value = "讨论帖子对应主题的唯一英文缩写", name = "discussionTopicSlug")
            @RequestParam(value = "discussionTopicSlug") String discussionTopicSlug,
            @ApiParam(value = "讨论帖子的标题", name = "discussionThreadTitle")
            @RequestParam(value = "discussionThreadTitle") String discussionThreadTitle,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.discussionClientService
				.editDiscussionThreadAction(discussionThreadId, discussionTopicSlug, discussionThreadTitle, csrfToken, request);
	}

	/**
	 * 处理用户创建讨论回复的请求.
	 * @param discussionThreadId - 讨论帖子的唯一标识符
	 * @param replyContent - 讨论回复的内容
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论回复创建结果的JSON对象
	 */
	@ApiOperation(value = "处理用户创建讨论回复的请求")
	@RequestMapping(value="/{threadId}/createDiscussionReply.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createDiscussionReplyAction(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "threadId")
            @PathVariable("threadId") long discussionThreadId,
            @ApiParam(value = "讨论回复的内容", name = "replyContent")
            @RequestParam(value = "replyContent") String replyContent,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.discussionClientService
				.createDiscussionReplyAction(discussionThreadId, replyContent, csrfToken, request);
	}

	/**
	 * 处理用户编辑讨论回复的请求.
	 * @param discussionReplyId - 讨论回复的唯一标识符
	 * @param replyContent - 讨论回复的内容
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论回复编辑结果的JSON对象
	 */
	@ApiOperation(value = "处理用户编辑讨论回复的请求")
	@RequestMapping(value="/{threadId}/editDiscussionReply.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData editDiscussionReplyAction(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "threadId")
            @PathVariable("threadId") long discussionThreadId,
            @ApiParam(value = "讨论回复的唯一标识符", name = "discussionReplyId")
            @RequestParam(value = "discussionReplyId") long discussionReplyId,
            @ApiParam(value = "讨论回复的内容", name = "replyContent")
            @RequestParam(value = "replyContent") String replyContent,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.discussionClientService
				.editDiscussionReplyAction(discussionThreadId, discussionReplyId, replyContent, csrfToken, request);
	}

	/**
	 * 处理用户删除讨论回复的请求.
	 * @param discussionReplyId - 讨论回复的唯一标识符
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpServletRequest对象
	 * @return 包含讨论回复删除结果的JSON对象
	 */
	@ApiOperation(value = "处理用户删除讨论回复的请求")
	@RequestMapping(value="/{threadId}/deleteDiscussionReply.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData deleteDiscussionReplyAction(
            @ApiParam(value = "讨论帖子的唯一标识符", name = "threadId")
            @PathVariable("threadId") long discussionThreadId,
            @ApiParam(value = "讨论回复的唯一标识符", name = "discussionReplyId")
            @RequestParam(value = "discussionReplyId") long discussionReplyId,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.discussionClientService
				.deleteDiscussionReplyAction(discussionThreadId, discussionReplyId, csrfToken, request);
	}

	@Autowired
	DiscussionClientService discussionClientService;
}
