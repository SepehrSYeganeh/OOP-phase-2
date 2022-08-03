package fxml.group;

import fxml.ControllerContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import jdbc.Database;

import java.io.IOException;
import java.sql.SQLException;

public class ChatController {
    private static String userID;
    private static String groupID;
    private static boolean admin;
    private static boolean owner;

    public static void setUserID(String userID) {
        ChatController.userID = userID;
    }
    public static void setGroupID(String groupID) {
        ChatController.groupID = groupID;
    }
    public static void setAdmin(boolean admin) {
        ChatController.admin = admin;
    }
    public static void setOwner(boolean owner) {
        ChatController.owner = owner;
    }

    private static final FXMLLoader fxmlLoader = new FXMLLoader(ControllerContext.class.getResource("chat.fxml"));
    private static Parent root;
    static {
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final Scene scene = new Scene(root);


    public static FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public static Parent getRoot() {
        return root;
    }

    public static Scene getScene() {
        return scene;
    }

    public static ChatController getController() {
        return fxmlLoader.getController();
    }

    //-----------------------------------------------------------------------------------------------------------------

    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem refreshItem;
    @FXML
    private MenuItem infoItem;
    @FXML
    private MenuItem membersItem;
    @FXML
    private MenuItem leaveItem;
    @FXML
    private MenuItem returnButton;
    @FXML
    private VBox messagesVBox;
    @FXML
    private TextArea textArea;
    @FXML
    private Button sendButton;
    @FXML
    private Label textErrLabel;


    public boolean init_menuBar(String userID, String groupID) {
        try {
            menuBar.getMenus().clear();

            if (Database.isOwner(userID, groupID)) {
                init_groupMenu();
                init_adminMenu();
                init_ownerMenu();
            }
            else if (Database.isAdmin(userID, groupID)) {
                init_groupMenu();
                init_adminMenu();
            }
            else init_groupMenu();

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void init_groupMenu() {
        MenuItem refresh = new MenuItem("messages");
        refresh.setOnAction(event -> init_groupMessages(groupID));

        MenuItem groupInfo = new MenuItem("Group info");
        groupInfo.setOnAction(event -> {
            try {
                GroupInfo.getController().display(groupID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MenuItem members = new MenuItem("Members");
        members.setOnAction(event -> {
            try {
                GroupMembers.getController().display(groupID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MenuItem leave = new MenuItem("Leave");

        MenuItem Return = new MenuItem("Return");
        Return.setOnAction(event -> {
            try {
                go_to_groups(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Menu menu = new Menu("Group");
        menu.getItems().addAll(refresh, groupInfo, members, leave, Return);
        menuBar.getMenus().add(menu);
    }

    private void init_adminMenu() {
        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(event -> {
            EditGroupController.setGroupID(groupID);
            ControllerContext.change_scene(EditGroupController.getScene());
        });

        MenuItem addMember = new MenuItem("Add member");

        MenuItem addAdmin = new MenuItem("Add admin");

        MenuItem kickMember = new MenuItem("Kick member");

        Menu menu = new Menu("Admin options");
        menu.getItems().addAll(edit, addMember, addAdmin, kickMember);
        menuBar.getMenus().add(menu);
    }

    private void init_ownerMenu() {
        MenuItem removeAdmin = new MenuItem("Remove admin");

        MenuItem kickAdmin = new MenuItem("Kick admin");

        MenuItem destroy = new MenuItem("Destroy");

        Menu menu = new Menu("Owner options");
        menu.getItems().addAll(removeAdmin, kickAdmin, destroy);
        menuBar.getMenus().add(menu);
    }

    public boolean init_groupMessages(String groupID) {
        try {
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void refresh(ActionEvent event) {
        init_groupMessages(groupID);
    }

    public void go_to_groups(ActionEvent event) throws SQLException {
        if (GroupsController.getController().init_groups(userID)) {
            messagesVBox.getChildren().clear();
            menuBar.getMenus().clear();
            setUserID(null);
            setGroupID(null);
            setAdmin(false);
            setOwner(false);
            ControllerContext.change_scene(GroupsController.getScene());
        }
    }
}
