package com.shaprj.javafx.util;
/*
 * Created by O.Shalaevsky on 05.06.2019
 */

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class FilesHelper {

    public static void openFile(Window window, Consumer<File> processor) {
        openFile(window, new String[]{}, Paths.get("").toAbsolutePath().toString(), processor);
    }

    private static FileChooser buildFileChooserWithInitDir(String initialDir) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(initialDir));
        return chooser;
    }

    public static void openFile(Window window, String[] extensions, String initialDir, Consumer<File> processor) {

        FileChooser chooser = buildFileChooserWithInitDir(initialDir);

        for (String extension : extensions) {
            String[] splitted = extension.split(":");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(splitted[0], splitted[1]);
            chooser.getExtensionFilters().add(extFilter);
        }

        File file = chooser.showOpenDialog(window);
        processor.accept(file);
    }

    public static void saveFile(Window window, String[] extensions, String initialDir, Consumer<Pair<File, FileChooser>> processor) {

        FileChooser chooser = buildFileChooserWithInitDir(initialDir);

        for (String extension : extensions) {
            String[] splitted = extension.split(":");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(splitted[0], splitted[1]);
            chooser.getExtensionFilters().add(extFilter);
        }

        File file = chooser.showSaveDialog(window);
        processor.accept(new Pair<>(file, chooser));
    }

    public static void openDirectory(Window window, String initialDir, Consumer<File> processor) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(initialDir));
        File file = chooser.showDialog(window);
        if (file != null) {
            processor.accept(file);
        }
    }

    public static String normalizePath(String src) {
        return src.replaceAll("\\\\", "/");
    }

}
