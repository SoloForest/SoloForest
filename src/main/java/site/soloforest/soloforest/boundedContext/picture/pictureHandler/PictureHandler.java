package site.soloforest.soloforest.boundedContext.picture.pictureHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.boundedContext.picture.entity.Picture;
import site.soloforest.soloforest.boundedContext.picture.repository.PictureRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class PictureHandler {
	private final PictureRepository pictureRepository;

	public RsData<Picture> parseFileInfo(MultipartFile multipartFile) throws IOException {
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String creatDate = simpleDateFormat.format(new Date());

		String absolutePath = new File("").getAbsolutePath() + "/";

		String path = "src/main/resources/static/images/" + creatDate;
		File file = new File(path);

		if (!multipartFile.isEmpty()) {
			String contentType = multipartFile.getContentType();
			String originalFileExtension;

			//확장자명 없을 경우 잘못된 파일
			if (ObjectUtils.isEmpty(contentType)) {
				return RsData.of("F-1", "잘못된 파일입니다.", null);
			} else {
				if (contentType.contains("image/jpeg"))
					originalFileExtension = ".jpg";
				else if (contentType.contains("image/png"))
					originalFileExtension = ".png";
				else {
					return RsData.of("F-2", "이미지 파일만 가능합니다.");
				}
			}

			if (!file.exists()) {
				//디렉토리 존재하지 않을 때 생성
				file.mkdirs();
			}

			String newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
			Picture picture = Picture.builder()
				.originalName(multipartFile.getOriginalFilename())
				.filePath("/images/" + creatDate + "/" + newFileName)
				.fileSize(multipartFile.getSize())
				.build();

			pictureRepository.save(picture);
			//저장된 파일로 변경하여 보여주기 위해
			file = new File(absolutePath + path + "/" + newFileName);
			multipartFile.transferTo(file);

			return RsData.of("S-1", "이미지 파일을 등록하였습니다.", picture);
		}
		return RsData.of("F-3", "파일 등록을 실패했습니다.");
	}
}
