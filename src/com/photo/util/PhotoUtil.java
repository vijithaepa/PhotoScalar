package com.photo.util;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.imageio.ImageIO;

public class PhotoUtil {

	private static final String IMG_SRC = "photo.src.location";

	private static final String IMG_DEST = "photo.dest.location";

	private static final String THUMB_IMG_HEIGHT = "photo.base.thumb.height";

	private static final String THUMB_IMG_WIDTH = "photo.base.thumb.width";

	// Property reader from above bean property keys
	private static Properties prop;

	static {
		prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("resources/application.properties");
			// load a properties file
			prop.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static Dimension THUMB_SIZE;

	/**
	 * Stores the resized image in a physical location.
	 * 
	 */
	public void storeImage(BufferedImage originalImage, String fileName) {

		try {
			String format = (fileName.endsWith(".png")) ? "png" : "jpg";

			BufferedImage scaledImg = scaleImage(originalImage);
			ImageIO.write(scaledImg, format, new File(getDestLocation() + "/"
					+ fileName));
			System.out.println(fileName + " is Scaled");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the BufferedImage of a Image File.
	 * 
	 * @param file
	 *            .
	 * @return BufferedImage.
	 */
	public static BufferedImage getBufferedImage(File file) {
		BufferedImage buffImg = null;
		try {
			buffImg = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffImg;
	}

	/**
	 * Return the BufferedImage of a Source image File in path ref.
	 * 
	 * @param file
	 *            .
	 * @return BufferedImage.
	 */
	public static BufferedImage getBufferedImage(String ref) {
		BufferedImage bimg = null;
		try {

			bimg = ImageIO.read(new File(ref));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bimg;
	}

	/**
	 * Resize the image to the original scale.
	 * 
	 * @param image
	 *            Original image.
	 * @return scaled image.
	 */
	public BufferedImage scaleImage(BufferedImage newImage) {
		THUMB_SIZE = getThumbSize();
		BufferedImage image = newImage;
		int oriW = image.getWidth();
		int oriH = image.getHeight();
		int desW = 0, desH = 0;

		if (oriW == oriH) {
			desW = THUMB_SIZE.width;
			desH = THUMB_SIZE.height;
		} else if (oriW > oriH) {
			desW = THUMB_SIZE.width;
			desH = (THUMB_SIZE.width * oriH) / oriW;
		} else if (oriW < oriH) {
			desH = THUMB_SIZE.height;
			desW = (THUMB_SIZE.height * oriW) / oriH;
		}

		BufferedImage resizedImage = new BufferedImage(desW, desH,
				image.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, desW, desH, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	public String getSrcLocation() {
		return prop.getProperty(IMG_SRC);
	}

	public String getDestLocation() {
		return prop.getProperty(IMG_DEST);
	}

	public int getThumbImgHeight() {
		return Integer.parseInt(prop.getProperty(THUMB_IMG_HEIGHT, "200"));
	}

	public int getThumbImgWidth() {
		return Integer.parseInt(prop.getProperty(THUMB_IMG_WIDTH, "200"));
	}

	private Dimension getThumbSize() {
		return new Dimension(getThumbImgWidth(), getThumbImgHeight());
	}

	public static void main(String[] args) {

		PhotoUtil pUtil = new PhotoUtil();
		String srcPath = pUtil.getSrcLocation();
		String destPath = pUtil.getDestLocation();
		File srcFolder = new File(srcPath);
		File[] allFiles = srcFolder.listFiles();
		if (allFiles != null) {
			for (final File fileEntry : allFiles) {
				if (fileEntry.isDirectory()) {
					System.out.println(fileEntry.getName());
				} else {
					System.out.println(destPath + "\\" + fileEntry.getName());
					pUtil.storeImage(getBufferedImage(srcPath + "\\"
							+ fileEntry.getName()), fileEntry.getName());
				}
			}
		} else {
			System.out.println("No files under : " + srcPath);
		}

	}
}