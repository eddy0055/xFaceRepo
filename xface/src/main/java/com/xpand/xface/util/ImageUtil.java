package com.xpand.xface.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtil {
	private static String UNKNOWN_IMAGE_TYPE = "unknown";	
	public static String resizeImage(String base64Img, String width, String height, String imageType) {
		return ImageUtil.resizeImage(base64Img, StringUtil.stringToInteger(width,120).intValue()
				, StringUtil.stringToInteger(height,120).intValue(), imageType);
	}
	public static String resizeImage(String base64Img, int width, int height, String imageType) {
		if (StringUtil.checkNull(base64Img)) {
			return "";
		}
		String arrayBase64Img[] = base64Img.split(",");
		if (arrayBase64Img==null || arrayBase64Img.length!=2) {
			return base64Img;
		}	
		if (imageType==null) {
			imageType = ImageUtil.getImageTypeFromBase64(base64Img);					
		}
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		String base64ImgTmp = null; 
		try {
			base64Img = arrayBase64Img[1];
			base64ImgTmp = base64Img;
			Base64 decoder = new Base64();
			byte[] imageByte = decoder.decode(base64Img);								
			bis = new ByteArrayInputStream(imageByte);
			BufferedImage origImage = ImageIO.read(bis);
			int type = origImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : origImage.getType();
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(origImage, 0, 0, width, height, null);
			g.dispose();
			bos = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, imageType, bos);
            imageByte = bos.toByteArray();
            base64Img = decoder.encodeAsString(imageByte);		            
		} catch (IOException e) {
			e.printStackTrace();
			return arrayBase64Img[0]+","+base64ImgTmp;
		} finally {
			ImageUtil.closeInputStream(bis);
			ImageUtil.closeOutputStream(bos);
		}
		return arrayBase64Img[0]+","+base64Img;
	}
	public static void closeInputStream(InputStream inputStream) {
		try {
			if (inputStream!=null) {
				inputStream.close();
			}
		}catch (Exception ex) {}
	}
	public static void closeOutputStream(OutputStream outputStream) {
		try {
			if (outputStream!=null) {
				outputStream.close();
			}
		}catch (Exception ex) {}
	}
	public static String getImageTypeFromBase64(String base64Img) {
		//data:image/jpeg;base64,xxxx
		if (StringUtil.checkNull(base64Img)) {
			return ImageUtil.UNKNOWN_IMAGE_TYPE;
		}		
		String arrayBase64Img[] = base64Img.split(",");
		if (arrayBase64Img==null || arrayBase64Img.length!=2) {
			return ImageUtil.UNKNOWN_IMAGE_TYPE;
		}
		int indexChar1 = arrayBase64Img[0].indexOf("/");
		if (indexChar1<0) {
			return ImageUtil.UNKNOWN_IMAGE_TYPE;
		}
		int indexChar2 = arrayBase64Img[0].indexOf(";", indexChar1+1);
		if (indexChar2<0) {
			return ImageUtil.UNKNOWN_IMAGE_TYPE;
		}else {
			return arrayBase64Img[0].substring(indexChar1+1, indexChar2);
		}
	}
	public static String getImageFromMultipartFile(MultipartFile image) {
		try {
			if (image==null) {
				return "";
			}else {
				//data:image/jpeg;base64,xxxx
				String base64Image = "data:"+image.getContentType()+";base64,"+Base64.encodeBase64String(image.getBytes());				
				return base64Image;
			}
		}catch (Exception ex) {
			return "";
		}			
	}
	public static String getBase64ImageFromByteArray(String imageName, byte[] imageData) {
		try {
			if (imageData==null||imageData.length < 1024) {
				return "";
			}else {
				String base64Image = "data:image/"+ImageUtil.getImageTypeFromFileName(imageName)+";base64,"+Base64.encodeBase64String(imageData);								
				return base64Image;
			}
		}catch (Exception ex) {
			return "";
		}
	}
	public static String getImageTypeFromFileName(String imageName) {
		if (StringUtil.checkNull(imageName)||imageName.length()<5) {
			return "jpeg";
		}
		int imageSize = imageName.length();
		String imageType = imageName.substring(imageSize-3, imageSize);
		if (imageType.equalsIgnoreCase("png")) {
			return "png";
		}else if (imageType.equalsIgnoreCase("gif")) {
			return "gif";
		}else {
			return "jpeg";
		}
	}
	public static String getImageTypeFromMultipartFile(MultipartFile image) {
		try {
			if (image==null) {
				return "";
			}else {
				//data:image/jpeg;base64,xxxx
				String base64Image = "data:"+image.getContentType()+";base64,"+Base64.encodeBase64String(image.getBytes());				
				return ImageUtil.getImageTypeFromBase64(base64Image);
			}
		}catch (Exception ex) {
			return "";
		}			
	}
	public static boolean base64ToFile(String base64Image, String fileName) {
		byte[] data = Base64.decodeBase64(base64Image);
		try {
			OutputStream stream = new FileOutputStream(fileName); 
		    stream.write(data);
		    stream.close();
		    return true;
		}catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public static String getBase64StringWithOutHeader(String base64Image) {
		try {
			//data:image/jpeg;base64,xxxx
			String arrayData[] = base64Image.split(",");
			if (arrayData==null) {
				return base64Image;
			}else if (arrayData.length!=2) {
				return base64Image;
			}else {
				return arrayData[1];
			}
		}catch (Exception ex) {
			ex.printStackTrace();
			return base64Image;
		}
	}
}
	
