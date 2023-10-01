/*
 * Copyright (c) JForum Team
 * All rights reserved.

 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided
 * that the following conditions are met:

 * 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor
 * the names of its contributors may be used to endorse
 * or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * This file creation date: 21/04/2004 - 19:54:16
 * The JForum Project
 * http://www.jforum.net
 */
package com.muling.common.util;

import cn.hutool.core.io.file.FileNameUtil;
import com.muling.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

/**
 * Utilities methods for image manipulation. It does not support writting of GIF images, but it can
 * read from. GIF images will be saved as PNG.
 *
 * @author Rafael Steil
 * @version $Id: ImageUtils.java,v 1.21 2006/11/12 13:25:23 rafaelsteil Exp $
 */
@Slf4j
public class ImageUtils
{
	public static final int IMAGE_UNKNOWN = -1;

	public static final int IMAGE_JPEG = 0;

	public static final int IMAGE_PNG = 1;

	public static final int IMAGE_GIF = 2;

	public static final Long IMAGE_URL = 100L;

	/**
	 * Resizes an image
	 *
	 * @param imgName The image name to resize. Must be the complet path to the file
	 * @param type int
	 * @param maxWidth The image's max width
	 * @param maxHeight The image's max height
	 * @return A resized <code>BufferedImage</code>
	 */
	public static BufferedImage resizeImage(String imgName, int type, int maxWidth, int maxHeight)
	{
		try {
			return resizeImage(ImageIO.read(new File(imgName)), type, maxWidth, maxHeight);
		}
		catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * Resizes an image.
	 *
	 * @param image
	 *            The image to resize
	 * @param maxWidth
	 *            The image's max width
	 * @param maxHeight
	 *            The image's max height
	 * @return A resized <code>BufferedImage</code>
	 * @param type
	 *            int
	 */
	public static BufferedImage resizeImage(BufferedImage image, int type, int maxWidth, int maxHeight)
	{
		Dimension largestDimension = new Dimension(maxWidth, maxHeight);

		// Original size
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		float aspectRation = (float) imageWidth / imageHeight;

		if (imageWidth > maxWidth || imageHeight > maxHeight) {
			if ((float) largestDimension.width / largestDimension.height > aspectRation) {
				largestDimension.width = (int) Math.ceil(largestDimension.height * aspectRation);
			}
			else {
				largestDimension.height = (int) Math.ceil(largestDimension.width / aspectRation);
			}

			imageWidth = largestDimension.width;
			imageHeight = largestDimension.height;
		}

		return createHeadlessSmoothBufferedImage(image, type, imageWidth, imageHeight);
	}

	/**
	 * Saves an image to the disk.
	 *
	 * @param image  The image to save
	 * @param toFileName The filename to use
	 * @param type The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as JPEG images,
	 *  or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
	 * @return <code>false</code> if no appropriate writer is found
	 */
	public static boolean saveImage(BufferedImage image, String toFileName, int type)
	{
		try {
			return ImageIO.write(image, type == IMAGE_JPEG ? "jpg" : "png", new File(toFileName));
		}
		catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * Compress and save an image to the disk. Currently this method only supports JPEG images.
	 *
	 * @param image The image to save
	 * @param toFileName The filename to use
	 * @param type The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as JPEG images,
	 * or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
	 */
	public static void saveCompressedImage(BufferedImage image, String toFileName, int type)
	{
		try {
			if (type == IMAGE_PNG) {
				throw new UnsupportedOperationException("PNG compression not implemented");
			}

			Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
			ImageWriter writer;
			writer = (ImageWriter) iter.next();

			ImageOutputStream ios = ImageIO.createImageOutputStream(new File(toFileName));
			writer.setOutput(ios);

			ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());

			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(0.7F);

			writer.write(null, new IIOImage(image, null, null), iwparam);

			ios.flush();
			writer.dispose();
			ios.close();
		}
		catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
	 * function on a completely headless system. This especially includes Linux and Unix systems
	 * that do not have the X11 libraries installed, which are required for the AWT subsystem to
	 * operate. This method uses nearest neighbor approximation, so it's quite fast. Unfortunately,
	 * the result is nowhere near as nice looking as the createHeadlessSmoothBufferedImage method.
	 *
	 * @param image  The image to convert
	 * @return The converted image
	 * @param type int
	 */
	public static BufferedImage createHeadlessBufferedImage(BufferedImage image, int type, int width, int height)
	{
		if (type == ImageUtils.IMAGE_PNG && hasAlpha(image)) {
			type = BufferedImage.TYPE_INT_ARGB;
		}
		else {
			type = BufferedImage.TYPE_INT_RGB;
		}

		BufferedImage bi = new BufferedImage(width, height, type);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				bi.setRGB(x, y, image.getRGB(x * image.getWidth() / width, y * image.getHeight() / height));
			}
		}

		return bi;
	}

	/**
	 * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
	 * function on a completely headless system. This especially includes Linux and Unix systems
	 * that do not have the X11 libraries installed, which are required for the AWT subsystem to
	 * operate. The resulting image will be smoothly scaled using bilinear filtering.
	 *
	 * @param source The image to convert
	 * @return The converted image
	 * @param type  int
	 */
	public static BufferedImage createHeadlessSmoothBufferedImage(BufferedImage source, int type, int width, int height)
	{
		if (type == ImageUtils.IMAGE_PNG && hasAlpha(source)) {
			type = BufferedImage.TYPE_INT_ARGB;
		}
		else {
			type = BufferedImage.TYPE_INT_RGB;
		}

		BufferedImage dest = new BufferedImage(width, height, type);

		int sourcex;
		int sourcey;

		double scalex = (double) width / source.getWidth();
		double scaley = (double) height / source.getHeight();

		int x1;
		int y1;

		double xdiff;
		double ydiff;

		int rgb;
		int rgb1;
		int rgb2;

		for (int y = 0; y < height; y++) {
			sourcey = y * source.getHeight() / dest.getHeight();
			ydiff = scale(y, scaley) - sourcey;

			for (int x = 0; x < width; x++) {
				sourcex = x * source.getWidth() / dest.getWidth();
				xdiff = scale(x, scalex) - sourcex;

				x1 = Math.min(source.getWidth() - 1, sourcex + 1);
				y1 = Math.min(source.getHeight() - 1, sourcey + 1);

				rgb1 = getRGBInterpolation(source.getRGB(sourcex, sourcey), source.getRGB(x1, sourcey), xdiff);
				rgb2 = getRGBInterpolation(source.getRGB(sourcex, y1), source.getRGB(x1, y1), xdiff);

				rgb = getRGBInterpolation(rgb1, rgb2, ydiff);

				dest.setRGB(x, y, rgb);
			}
		}

		return dest;
	}

	private static double scale(int point, double scale)
	{
		return point / scale;
	}

	private static int getRGBInterpolation(int value1, int value2, double distance)
	{
		int alpha1 = (value1 & 0xFF000000) >>> 24;
		int red1 = (value1 & 0x00FF0000) >> 16;
		int green1 = (value1 & 0x0000FF00) >> 8;
		int blue1 = (value1 & 0x000000FF);

		int alpha2 = (value2 & 0xFF000000) >>> 24;
		int red2 = (value2 & 0x00FF0000) >> 16;
		int green2 = (value2 & 0x0000FF00) >> 8;
		int blue2 = (value2 & 0x000000FF);

		int rgb = ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << 24)
			| ((int) (red1 * (1.0 - distance) + red2 * distance) << 16)
			| ((int) (green1 * (1.0 - distance) + green2 * distance) << 8)
			| (int) (blue1 * (1.0 - distance) + blue2 * distance);

		return rgb;
	}

	/**
	 * Determines if the image has transparent pixels.
	 *
	 * @param image The image to check for transparent pixel.s
	 * @return <code>true</code> of <code>false</code>, according to the result
	 */
	public static boolean hasAlpha(Image image)
	{
		try {
			PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
			pg.grabPixels();

			return pg.getColorModel().hasAlpha();
		}
		catch (InterruptedException e) {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {

		// 截全屏幕保存成文件
//		fullScreenShotAsFile("/Users/hank/Downloads/","sceenshot","jpg");
//
		File imageFile = new File("/Users/ooo/Downloads/sceenshot.jpg");
//		// 自动打开图片
//		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
//			Desktop.getDesktop().open(imageFile);
//		}
		String savePath = "/Users/ooo/Downloads/";
//		String fileName =  "cut";
//		String format = "jpg";
//		BufferedImage bfImage = cutBufferedImage(ImageIO.read(imageFile),200,100,1000,500);
		File path = new File(savePath);
//		File file = new File(path, fileName+ "." + format);
//		ImageIO.write(bfImage, format, file);


		System.out.println(FileNameUtil.getName("http://192.168.11.42/card/Card01.png"));
		System.out.println(FileNameUtil.getPrefix("http://192.168.11.42/card/Card01.png"));
		System.out.println(FileNameUtil.extName("http://192.168.11.42/card/Card01.png"));
		System.out.println(FileNameUtil.getSuffix("http://192.168.11.42/card/Card01.png"));
		System.out.println(FileNameUtil.mainName("http://192.168.11.42/card/Card01.png"));
	}

	public static void resizeAndCutCardImage(String source,  String target){
		String  extName = FileNameUtil.extName(source);
		int type =  IMAGE_PNG;
		switch (extName){
			case "png":  type = IMAGE_PNG; break;
			case "jpg":  type = IMAGE_JPEG; break;
			case "jpeg":  type = IMAGE_JPEG; break;
			case "gif":  type = IMAGE_GIF; break;
		}
		BufferedImage image  =  resizeImage(source,type,196,328);
		BufferedImage cutImg = cutBufferedImage(image,28,10,140,140);
		File cf = new File(target);
		try {
			ImageIO.write(cutImg, extName, cf);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 指定屏幕区域截图，返回截图的BufferedImage对象
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage getScreenShot(int x, int y, int width, int height) {
		BufferedImage bfImage = null;
		try {
			Robot robot = new Robot();
			bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return bfImage;
	}

	/**
	 * 全屏幕区域截图，返回截图的BufferedImage对象
	 * @return
	 */
	public BufferedImage getFullScreenShot() {
		BufferedImage bfImage = null;
		try {
			Robot robot = new Robot();
			// 获取当前显示器屏幕大小
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			bfImage = robot.createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return bfImage;
	}

	/**
	 * 指定屏幕区域截图，保存到指定目录
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param savePath - 文件保存路径
	 * @param fileName - 文件保存名称
	 * @param format - 文件格式
	 */
	public void screenShotAsFile(int x, int y, int width, int height, String savePath, String fileName, String format) {
		try {
			Robot robot = new Robot();
			BufferedImage bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
			File path = new File(savePath);
			File file = new File(path, fileName+ "." + format);
			ImageIO.write(bfImage, format, file);
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 全屏幕区域截图，保存到指定目录
	 * @param savePath - 文件保存路径
	 * @param fileName - 文件保存名称
	 * @param format - 文件格式
	 */
	public static void fullScreenShotAsFile (String savePath,String fileName,String format) {
		try {
			Robot robot = new Robot();
			// 获取当前显示器屏幕大小
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			//拷贝屏幕到一个BufferedImage对象screenshot
			BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
			File path = new File(savePath);
			File file = new File(path, fileName+ "." + format);
			ImageIO.write(bfImage, format, file);
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * BufferedImage图片剪裁
	 * @param srcBfImg - 被剪裁的BufferedImage
	 * @param x - 左上角剪裁点X坐标
	 * @param y - 左上角剪裁点Y坐标
	 * @param width - 剪裁出的图片的宽度
	 * @param height - 剪裁出的图片的高度
	 * @return 剪裁得到的BufferedImage
	 */
	public static BufferedImage cutBufferedImage(BufferedImage srcBfImg, int x, int y, int width, int height) {
		BufferedImage cutedImage = null;
		CropImageFilter cropFilter = new CropImageFilter(x, y, width, height);
		Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(srcBfImg.getSource(), cropFilter));
		cutedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = cutedImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return cutedImage;
	}
}
