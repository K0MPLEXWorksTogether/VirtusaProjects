package tech.abhirammangipudi.ui.user;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import tech.abhirammangipudi.errors.ResourceAlreadyExistsException;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.services.UserService;
import tech.abhirammangipudi.ui.BankingApp;

public class SignupPage extends JPanel {
    public SignupPage(BankingApp app, UserService userService) {
        setLayout(new GridLayout(10, 2));

        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField dob = new JTextField();
        JTextField email = new JTextField();
        JTextField address = new JTextField();
        JTextField phone = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JButton create = new JButton("Create Account");
        JButton back = new JButton("Back");

        create.addActionListener(e -> {
            String fName = firstName.getText();
            String lName = lastName.getText();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dateOfBirth = LocalDate.parse(dob.getText(), formatter).atStartOfDay();
            ;

            String mail = email.getText();
            String addr = address.getText();
            String phoneNumber = address.getText();
            String uname = username.getText();
            String pwd = new String(password.getPassword());

            User newUser = new User(fName, lName, dateOfBirth, mail, addr, phoneNumber, uname, pwd, null);
            try {
                userService.register(newUser);
                JOptionPane.showMessageDialog(this, "Login successful!");
            } catch (ResourceAlreadyExistsException rae) {
                JOptionPane.showConfirmDialog(this, rae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            app.showPage("login");
        });

        back.addActionListener(e -> app.showPage("login"));

        add(new JLabel("First Name:"));
        add(firstName);
        add(new JLabel("Last Name:"));
        add(lastName);
        add(new JLabel("DOB:"));
        add(dob);
        add(new JLabel("Email:"));
        add(email);
        add(new JLabel("Address:"));
        add(address);
        add(new JLabel("Phone:"));
        add(phone);
        add(new JLabel("Username:"));
        add(username);
        add(new JLabel("Password:"));
        add(password);
        add(create);
        add(back);
    }
}
