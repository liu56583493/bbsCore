package com.bbs.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

/**
 * 图片放大缩小,以及压缩，取宽度即可
 * @title ImageUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年5月11日下午5:13:23
 */
public class ImageUtils {

	/**
	 * 图片放大缩小,以及压缩
	 * 追求最好的效果
	 * @param file
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage resizeImage(BufferedImage src, int width, int height) throws IOException {
		// Setup the rendering resources to match the source image's
		BufferedImage dist = new BufferedImage(width, height, (src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB));
		Image image = src.getScaledInstance( width, height, Image.SCALE_SMOOTH );

		Graphics2D g2 = dist.createGraphics();

		// Scale the image to the new buffer using the specified rendering hint.
		g2.drawImage(image, 0, 0, width, height, null);

		// Scale the image to the new buffer using the specified rendering hint.
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		// Just to be clean, explicitly dispose our temporary graphics object
		g2.dispose();
		// Return the scaled image to the caller.
		return dist;
	}

	/**
	 * 图片放大缩小,以及压缩，取宽度即可
	 * 对于gif和图片本身小于限定大小的略过resize
	 * @param file
	 * @param width
	 * @param height
	 * @throws IOException 
	 */
	public static void resize(File src, File dist, int width) throws IOException {
		// 先判断文件夹是否存在，不存在时则创建整个目录
		File parentDirectory = dist.getParentFile();
		if (!parentDirectory.exists()) {
			parentDirectory.mkdirs();
		}
		// 拒绝resize、GIF，直接copy
		if (src.getName().toLowerCase().endsWith(".gif")) {
			FileUtils.copyFile(src, dist);
			return;
		}
		// 读取图片
		BufferedImage bufferedImage = ImageIO.read(src);
		int w = bufferedImage.getWidth(null);
		int h = bufferedImage.getHeight(null);
		// 比例，取宽度比，转换double，更精确的比例
		double rate = (double)width / w;
		if (rate <= 1) {
			FileUtils.copyFile(src, dist);
			return;
		}
		// 图片缩放
		BufferedImage out = resizeImage(bufferedImage, width, (int)(h * rate));
		// 生成图片
		ImageIO.write(out, "JPEG", dist);
	}

}
