<?php
require 'connection.php';

$response = array();

if(isset($_POST['username'])) {
    $user = $_POST['username'];
    $pass = $_POST['password'];
    $name = $_POST['name'];
    $number = $_POST['number'];
    $email = $_POST['email'];
    $gender = $_POST['gender'];
    $birth = $_POST['birthday'];
    $address = $_POST['address'];

    $position = "user";

    $stmt1 = $conn->prepare("SELECT username FROM usertbl WHERE username = ?");
    $stmt1->bind_param("s", $user);
    $stmt1->execute();
    $stmt1->store_result();
    if($stmt1->num_rows > 0)
    {
        $response['error'] = true;
        $response['message'] = "This username is already taken";
        echo json_encode($response);
        exit();
    }

    $stmt2 = $conn->prepare("SELECT number FROM usertbl WHERE number = ?");
    $stmt2->bind_param("i", $number);
    $stmt2->execute();
    $stmt2->store_result();
    if($stmt2->num_rows > 0)
    {
        $response['error'] = true;
        $response['message'] = "This number is already created";
        echo json_encode($response);
        exit();
    }
   

    $stmt = $conn->prepare("INSERT INTO usertbl (username, password, name, number, email, gender, birthday, address, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sssssssss", $user, $pass, $name, $number, $email, $gender, $birth, $address, $position);

    if($stmt->execute() == TRUE){
        $response['error'] = false;

        date_default_timezone_set("Asia/Manila");
        $date = date("d/m/y");
        $status = "Active";

        
        $uid = $stmt->insert_id;//retrieve last inserted
        $balance = "0";
        $stmt3 = $conn->prepare("INSERT INTO userwallettbl (uid, balance, date, status) VALUES (?, ?, ?, ?)");
        $stmt3->bind_param("isss", $uid, $balance, $date, $status);
        $stmt3->execute();



        $response['message'] = "Data Created Successfully!";
    } else {
        $response['error'] = true;
        $response['message'] = "Failed!" . $conn->error;
    }
} else {
    $response['error'] = true;
    $response['message'] = "Insufficient Parameters.";
}

echo json_encode($response);



?>