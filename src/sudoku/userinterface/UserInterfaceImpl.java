package sudoku.userinterface;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sudoku.constants.GameState;
import sudoku.problemdomain.Coordinates;
import sudoku.problemdomain.SudokuGame;

import java.util.HashMap;

public class UserInterfaceImpl implements IUserInterfaceContract.View,
        EventHandler<KeyEvent> {

    private final Stage stage;
    private final Group root;

    //To keep track of 81 different text fields I use HashMaps
    private HashMap<Coordinates, SudokuTextField> textFieldCoordinates;

    private IUserInterfaceContract.EventListener listener;

    private static final double WINDOW_X = 500;
    private static final double WINDOW_Y = 550;
    private static final double BOARD_X_AND_Y = 450;
    private static final double BOARD_PADDING = 25;

    private static final Color WINDOW_BACKGROUND_COLOR = Color.rgb(0, 72, 216);
    private static final Color BOARD_BACKGROUND_COLOR = Color.rgb(255, 255, 255);
    private static final String SUDOKU = "Sudoku";

    public UserInterfaceImpl(Stage stage) {
        this.stage = stage;
        this.root = new Group();
        this.textFieldCoordinates = new HashMap<>();
        initializeUserInterface();
    }

    // Using helper methods to avoid a large method
    private void initializeUserInterface() {
        drawBackground(root);
        drawTitle(root);
        drawSudokuBoard(root);
        drawTextFields(root);
        drawGridLines(root);
        stage.show();
    }
    // In order to draw the various lines that make up the Sudoku grid, I used a starting x and y offset
    // value.
    private void drawGridLines(Group root) {
        //Drawing vertical lines starting at 114x and 114y:
        int xAndY = 80;
        int index = 0;
        while (index < 8) {
            int thickness;
            if (index == 2 || index == 5) {
                thickness = 3;
            } else {
                thickness = 2;
            }

            Rectangle verticalLine = getLine(
                    xAndY + 50 * index,
                    BOARD_PADDING,
                    BOARD_X_AND_Y,
                    thickness
            );

            Rectangle horizontalLine = getLine(
                    BOARD_PADDING,
                    xAndY + 50 * index,
                    thickness,
                    BOARD_X_AND_Y
            );

            root.getChildren().addAll(
                    verticalLine,
                    horizontalLine
            );

            index++;
        }
    }

    public Rectangle getLine(double x, double y, double height, double width){
        Rectangle line = new Rectangle();

        line.setX(x);
        line.setY(y);

        line.setHeight(height);
        line.setWidth(width);

        line.setFill(Color.BLACK);
        return line;
    }

    /**
     * 1. Based on x and y values, each TextField is drawn.
     * 2. As each TextField is drawn, its coordinates (x, y) are added based on its Hash Value to
     * the HashMap.
     */
    private void drawTextFields(Group root) {
        //where to start drawing the numbers
        final int xOrigin = 30;
        final int yOrigin = 30;
        //how much to move the x or y value after each loop
        final int xAndYDelta = 50;

        for (int xIndex = 0; xIndex < 9; xIndex++) {
            for (int yIndex = 0; yIndex < 9; yIndex++) {
                int x = xOrigin + xIndex * xAndYDelta;
                int y = yOrigin + yIndex * xAndYDelta;
                //draw it
                SudokuTextField tile = new SudokuTextField(xIndex, yIndex);

                //encapsulated style info
                styleSudokuTile(tile, x, y);

                //Note: Note that UserInterfaceImpl implements EventHandler<ActionEvent> in the class declaration.
                //By passing "this" (which means the current instance of UserInterfaceImpl), when an action occurs,
                //it will jump straight to "handle(ActionEvent actionEvent)" down below.
                tile.setOnKeyPressed(this);

                textFieldCoordinates.put(new Coordinates(xIndex, yIndex), tile);

                root.getChildren().add(tile);
            }
        }
    }
    // Helper method for styling a sudoku tile number
    private void styleSudokuTile(SudokuTextField tile, double x, double y) {
        Font numberFont = new Font(24);
        tile.setFont(numberFont);
        tile.setAlignment(Pos.CENTER);

        tile.setLayoutX(x);
        tile.setLayoutY(y);
        tile.setPrefHeight(50);
        tile.setPrefWidth(50);

        tile.setBackground(Background.EMPTY);

    }

    // Background of the actual sudoku board
    private void drawSudokuBoard(Group root) {
        Rectangle boardBackground = new Rectangle();
        boardBackground.setX(BOARD_PADDING);
        boardBackground.setY(BOARD_PADDING);
        boardBackground.setWidth(BOARD_X_AND_Y);
        boardBackground.setHeight(BOARD_X_AND_Y);
        boardBackground.setFill(BOARD_BACKGROUND_COLOR);
        root.getChildren().addAll(boardBackground);
    }

    private void drawTitle(Group root) {
        Text title = new Text(190, 525, SUDOKU);
        title.setFill(Color.WHITE);
        Font titleFont = new Font(35);
        title.setFont(titleFont);
        root.getChildren().add(title);
    }

    // Background of the main window
    private void drawBackground(Group root) {
        Scene scene = new Scene(root, WINDOW_X, WINDOW_Y);
        scene.setFill(WINDOW_BACKGROUND_COLOR);
        stage.setScene(scene);
    }

    @Override
    public void setListener(IUserInterfaceContract.EventListener listener) {
        this.listener = listener;
    }

    /**
    * Whenever the user enters an input (including 0 to remove a digit),
    * the user interface is updated accordingly.
    */
    @Override
    public void updateSquare(int x, int y, int input) {
        SudokuTextField tile = textFieldCoordinates.get(new Coordinates(x, y));
        String value = Integer.toString(
                input
        );

        if (value.equals("0")) value = "";

        tile.textProperty().setValue(value);
    }

    @Override
    public void updateBoard(SudokuGame game) {
        for (int xIndex = 0; xIndex < 9; xIndex++) {
            for (int yIndex = 0; yIndex < 9; yIndex++) {
                TextField tile = textFieldCoordinates.get(new Coordinates(xIndex, yIndex));

                String value = Integer.toString(
                        game.getCopyOfGridState()[xIndex][yIndex]
                );

                if (value.equals("0")) value = "";
                tile.setText(value);

                //If a given tile has a non-zero value and the state of the game is GameState.NEW, then the
                //tile is marked as read only. Otherwise, it will NOT be read only.
                if (game.getGameState() == GameState.NEW){
                    if (value.isEmpty()) {
                        tile.setStyle("-fx-text-fill: red;");
                        tile.setDisable(false);
                    } else {
                        tile.setStyle("-fx-opacity: 0.8");
                        tile.setDisable(true);
                    }
                }
            }
        }
    }

    @Override
    public void showDialog(String message) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
        dialog.showAndWait();

        if (dialog.getResult() == ButtonType.OK) listener.onDialogClick();
    }

    @Override
    public void showError(String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        dialog.showAndWait();
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (
                    event.getText().matches("[0-9]")

            ) {
                int value = Integer.parseInt(event.getText());
                handleInput(value, event.getSource());
                // Set text color to red for new input
                ((TextField) event.getSource()).setStyle("-fx-text-fill: red;");
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                handleInput(0, event.getSource());
                // Set text color to black for empty input
                ((TextField) event.getSource()).setStyle("-fx-text-fill: black;");
            } else {
                ((TextField)event.getSource()).setText("");
            }
        }
        event.consume();
    }

    /**
     * @param value  expected to be an integer from 0-9, inclusive
     * @param source the textfield object that was clicked.
     */
    private void handleInput(int value, Object source) {
        listener.onSudokuInput(
                ((SudokuTextField) source).getX(),
                ((SudokuTextField) source).getY(),
                value
        );
    }
}
