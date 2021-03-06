package com.ssafy.api.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.api.request.FileSavePostReq;
import com.ssafy.api.request.HomeworkCreatePostReq;
import com.ssafy.api.request.HomeworkPutReq;
import com.ssafy.api.response.HomeworkListRes;
import com.ssafy.api.response.HomeworkNNickname;
import com.ssafy.api.response.HomeworkRes;
import com.ssafy.api.response.UserHomeworkInfo;
import com.ssafy.api.response.UserHomeworkListRes;
import com.ssafy.api.service.AwsS3Service;
import com.ssafy.api.service.HomeworkService;
import com.ssafy.api.service.UserHomeworkService;
import com.ssafy.api.service.UserService;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.common.util.MD5Generator;
import com.ssafy.db.entity.Homework;
import com.ssafy.db.entity.UserHomework;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Homework API", tags = {"Homework"})
@RestController
@RequestMapping("/api/v1/homework")
public class HomeworkController {
	@Autowired
	HomeworkService homeworkService;
	@Autowired
	UserService userService;
	@Autowired
	UserHomeworkService userHomeworkService;
	@Autowired
	AwsS3Service awsS3Service;
	
	@PostMapping("/create")
	@ApiOperation(value = "?????? ??????", notes = "?????? ?????? ????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<? extends BaseResponseBody> create(
			@RequestBody @ApiParam(value="????????? ?????? ??????", required = true) HomeworkCreatePostReq HomeworkInfo){	
		if(homeworkService.createHomework(HomeworkInfo))
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "??????"));
		else
			return ResponseEntity.status(401).body(BaseResponseBody.of(401, "??????"));	
	}
	
	@GetMapping("/homeworklist/{studyno}")
	@ApiOperation(value = "?????? ??? ?????????", notes = "????????? ?????? ?????? ??? ???????????? ???????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<HomeworkListRes> getHomeworkList(@PathVariable("studyno") @ApiParam(value = "study pk", required = true) int studyno){
		List<Homework> homeworkList = homeworkService.getHomeworkList(studyno);
		List<HomeworkNNickname> homeworkNNicknameList = new ArrayList<>();
		
		for(int i = 0; i < homeworkList.size(); i++) {
			homeworkNNicknameList.add(new HomeworkNNickname(homeworkList.get(i), userService.getUserNickname(homeworkList.get(i).getUserno())));
		}
		
		return ResponseEntity.status(200).body(HomeworkListRes.of(homeworkNNicknameList));
	};
	
	@GetMapping("/homework/detail/{homeworkno}")
	@ApiOperation(value = "?????? ??? ????????????", notes = "?????? ??? ??????????????? ???????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<HomeworkRes> getStudyBoardDetail(@PathVariable("homeworkno") @ApiParam(value = "homework pk", required = true) int homeworkno){
		Homework homework = homeworkService.getSHomeworkDetail(homeworkno);
		String nickname = userService.getUserNickname(homework.getUserno());
		return ResponseEntity.status(200).body(HomeworkRes.of(homework, nickname));
	};

	
	@PutMapping("/modify")
	@ApiOperation(value = "?????? ??? ??????", notes = "?????? ?????? ????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<? extends BaseResponseBody> accept(
			@RequestBody @ApiParam(value="?????? ??? ?????? ??????", required = true) HomeworkPutReq homeworkPutInfo){
		if(homeworkService.modifyHomework(homeworkPutInfo))
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "??????"));
		else
			return ResponseEntity.status(401).body(BaseResponseBody.of(401, "??????"));
	}
	
	@DeleteMapping("/delete/{homeworkno}")
	@ApiOperation(value = "?????? ??? ??????", notes = "?????? ?????? ????????????.") 
    @ApiResponses({
        @ApiResponse(code = 200, message = "??????"),
        @ApiResponse(code = 401, message = "??????"),
        @ApiResponse(code = 500, message = "?????? ??????")
    })
	public ResponseEntity<? extends BaseResponseBody> deleteHomework(
			@PathVariable("homeworkno") @ApiParam(value = "????????? homework pk", required = true) int homeworkno){
		if(homeworkService.deletehomework(homeworkno))
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
		else
			return ResponseEntity.status(401).body(BaseResponseBody.of(401, "??????"));
	}
	
	@PostMapping("/upload")
	@ApiOperation(value = "???????????? ?????????", notes = "????????? ???????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<? extends BaseResponseBody> fileUpload(
			@RequestParam @ApiParam(value="?????? no", required = true) int homeworkno,
			@RequestParam @ApiParam(value="?????? no", required = true) int userno,
			@RequestPart @ApiParam(value="???????????? ??????", required = true) MultipartFile files){
		try {
			String ogname = files.getOriginalFilename();
			String extention = FilenameUtils.getExtension(ogname);
			String newfilename = new MD5Generator(FilenameUtils.getBaseName(ogname)).toString();
			
			String filePath = awsS3Service.upload(files, "homework", newfilename+"."+extention);
			FileSavePostReq file = new FileSavePostReq();
			file.setFilepath(filePath);
			file.setFilename("test");
			file.setOgfilename(files.getOriginalFilename());
            files.getOriginalFilename();

            userHomeworkService.saveFile(file, userno, homeworkno);
            userService.countUpHomework(userno);
        } catch(Exception e) {
            e.printStackTrace();
        }
		
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}
	
	@GetMapping("/download/{fileno}")
	@ApiOperation(value = "???????????? ????????????", notes = "????????? ??????????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<byte[]> fileDownload(
			@PathVariable("fileno") @ApiParam(value = "??????????????? ?????? no", required = true) int fileno) throws IOException {
		UserHomework userHomework = userHomeworkService.getFile(fileno);
		return awsS3Service.getObject(userHomework.getFilepath(), userHomework.getOgfilename());
	}
	
	@GetMapping("/uploadList/{homeworkno}")
	@ApiOperation(value = "????????? ????????? ??????", notes = "????????? ???????????? ????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<UserHomeworkListRes> uploadHomeworkList(
			@PathVariable("homeworkno") @ApiParam(value = "????????? ????????? homeworkno", required = true) int homeworkno) throws IOException {
		
		List<UserHomeworkInfo> userHomeworkInfoList = new ArrayList<UserHomeworkInfo>();
		List<UserHomework> userHomeworkList = homeworkService.getUploadHomework(homeworkno);
		for(int i = 0; i < userHomeworkList.size(); i++) {
			userHomeworkInfoList.add(new UserHomeworkInfo(userHomeworkList.get(i), userService.getUserByUserno(userHomeworkList.get(i).getUserno()).getNickname()));
		}
		return ResponseEntity.status(200).body(UserHomeworkListRes.of(userHomeworkInfoList));
	}
	
	@GetMapping("/filedelete/{fileno}")
	@ApiOperation(value = "???????????? ??????", notes = "????????? ????????????.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "??????"),
		@ApiResponse(code = 401, message = "??????"),
		@ApiResponse(code = 500, message = "?????? ??????")
	})
	public ResponseEntity<? extends BaseResponseBody> fileDelete(
			@PathVariable("fileno") @ApiParam(value = "????????? ?????? no", required = true) int fileno) throws IOException {
		String filename = userHomeworkService.getFile(fileno).getFilepath();
		try {
			awsS3Service.deleteFile(filename);
		}catch(Exception e) {
			System.out.println("delete file error"+e.getMessage());
		}
		userHomeworkService.deleteFile(fileno);
		
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}
}
