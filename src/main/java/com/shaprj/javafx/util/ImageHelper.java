package com.shaprj.javafx.util;
/*
 * Created by O.Shalaevsky on 10.06.2019
 */

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class ImageHelper {
    public static ImageView createImageViewFromResource(String path, Class<?> aClass) {
        InputStream input = aClass.getResourceAsStream(path);
        Image image = new Image(input, 32, 32, true, true);
        ImageView imageView = new ImageView(image);
        return imageView;
    }

    public static ImageView createTreeViewIconFromResource(double v, double v1, String path, Class<?> aClass) {
        InputStream input = aClass.getResourceAsStream(path);
        Image image = new Image(input, v, v1, true, true);
        ImageView imageView = new ImageView(image);
        return imageView;
    }

    public static ImageView createImageViewFromPath(String path, int width, int height) {
        return new ImageView(new Image(new File(path).toURI().toString(), width, height, true, true));
    }

    public static ImageView createImageFromByteInputStream(ByteArrayInputStream stream, int width, int height) {
        return new ImageView(new Image(stream, width, height, true, true));
    }
}
