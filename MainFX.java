package com.va.lab5.sa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Connect to database
        DBConnection.getConnection();

        BorderPane root = new BorderPane();

        // ===== Top (HBox with hockey image) =====
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));

        ImageView imageView = new ImageView(new Image("file:src/com/va/lab5/sa/images/hockey.jpg"));
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);
        topBox.getChildren().add(imageView);
        root.setTop(topBox);

        // ===== Bottom (HBox with today’s date) =====
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        Label dateLabel = new Label("Today's Date: " + LocalDate.now());
        bottomBox.getChildren().add(dateLabel);
        root.setBottom(bottomBox);

        // ===== Center (Employment Application Form) =====
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField email = new TextField();
        TextField website = new TextField();
        TextField position = new TextField();
        TextField salary = new TextField();
        TextField startDate = new TextField();
        TextField phone = new TextField();
        TextField fax = new TextField();
        TextField company = new TextField();
        TextArea comments = new TextArea();

        ToggleGroup relocateGroup = new ToggleGroup();
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");
        RadioButton unsure = new RadioButton("Not sure");
        yes.setToggleGroup(relocateGroup);
        no.setToggleGroup(relocateGroup);
        unsure.setToggleGroup(relocateGroup);

        form.add(new Label("First Name *"), 0, 0);
        form.add(firstName, 1, 0);
        form.add(new Label("Last Name *"), 2, 0);
        form.add(lastName, 3, 0);

        form.add(new Label("Email *"), 0, 1);
        form.add(email, 1, 1, 3, 1);

        form.add(new Label("Portfolio website"), 0, 2);
        form.add(website, 1, 2, 3, 1);

        form.add(new Label("Position you are applying for *"), 0, 3);
        form.add(position, 1, 3, 3, 1);

        form.add(new Label("Salary requirements"), 0, 4);
        form.add(salary, 1, 4);
        form.add(new Label("When can you start?"), 2, 4);
        form.add(startDate, 3, 4);

        form.add(new Label("Phone *"), 0, 5);
        form.add(phone, 1, 5);
        form.add(new Label("Fax"), 2, 5);
        form.add(fax, 3, 5);

        form.add(new Label("Are you willing to relocate?"), 0, 6);
        HBox relocateBox = new HBox(10, yes, no, unsure);
        form.add(relocateBox, 1, 6, 3, 1);

        form.add(new Label("Last company you worked for"), 0, 7);
        form.add(company, 1, 7, 3, 1);

        form.add(new Label("Reference / Comments / Questions"), 0, 8);
        form.add(comments, 1, 8, 3, 1);

        // === Buttons Section ===
        Button submitBtn = new Button("Submit");
        Button readBtn = new Button("Read");

        // --- Submit Button: Insert record into DB ---
        submitBtn.setOnAction(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO applicants (firstname, lastname, email, position, phone, comments) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, firstName.getText());
                stmt.setString(2, lastName.getText());
                stmt.setString(3, email.getText());
                stmt.setString(4, position.getText());
                stmt.setString(5, phone.getText());
                stmt.setString(6, comments.getText());
                stmt.executeUpdate();
                System.out.println("Record inserted successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // --- Read Button: Retrieve data from DB ---
        readBtn.setOnAction(e -> {
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM applicants")) {

                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id")).append(" | ");
                    sb.append("Name: ").append(rs.getString("firstname")).append(" ").append(rs.getString("lastname")).append(" | ");
                    sb.append("Email: ").append(rs.getString("email")).append(" | ");
                    sb.append("Position: ").append(rs.getString("position")).append(" | ");
                    sb.append("Phone: ").append(rs.getString("phone")).append("\n");
                }
                comments.setText(sb.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Add buttons to form
        HBox buttonBox = new HBox(10, submitBtn, readBtn);
        buttonBox.setAlignment(Pos.CENTER);
        form.add(buttonBox, 1, 9);

        root.setCenter(form);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Employment Application Form - Sedra Alkero");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}