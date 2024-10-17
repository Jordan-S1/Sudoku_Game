package sudoku;

import javafx.application.Application;
import javafx.stage.Stage;
import sudoku.buildlogic.SudokuBuildLogic;
import sudoku.userinterface.IUserInterfaceContract;
import sudoku.userinterface.UserInterfaceImpl;

import java.io.IOException;

public class SudokuApp extends Application {
    private IUserInterfaceContract.View uiI;

    @Override
    public void start(Stage primaryStage) throws Exception {
        uiI = new UserInterfaceImpl(primaryStage);
        try {
            SudokuBuildLogic.build(uiI);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}