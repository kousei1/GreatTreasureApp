<?php
require 'connection.php';

// an array to display response
$response = array();

if (isset($_POST['username'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];
    
    // Modify the SQL query to select additional user details
    $stmt = $conn->prepare("SELECT username, password, uid, name, number, position FROM usertbl WHERE username = ? AND password = ? OR number = ? OR email = ?");
    $stmt->bind_param("ssss", $username, $password, $username, $username);
    $stmt->execute();
    $stmt->store_result();
    
    if ($stmt->num_rows > 0) {
        // Bind the additional result variables
        $stmt->bind_result($username, $password, $uid, $name, $number, $position);
        $stmt->fetch();

            $response['error'] = false;
            $response['message'] = "login Successful!";
            // Add the additional user details to the response
            $response['username'] = $username;
            $response['password'] = $password;
            $response['uid'] = $uid;
            $response['name'] = $name;
            $response['number'] = $number;
            $response['position'] = $position;
        
    } else {
        $response['error'] = true;
        $response['message'] = "Incorrect username or password.";
    }
    
    $stmt->close();
} else {
    $response['error'] = true;
    $response['message'] = "Insufficient parameters.";
}

echo json_encode($response);





?>