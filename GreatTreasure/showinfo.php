<?php
require 'connection.php';

$response = array();

if (isset($_POST['uid'])) {
    $uid = $_POST['uid'];

    $stmt = $conn->prepare("SELECT uid, username, name, number, email, gender, birthday, address FROM usertbl WHERE uid = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($uid, $username, $name, $number, $email, $gender, $birth, $address);
        $stmt->fetch();
        $response['uid'] = $uid;
        $response['name'] = $name;
        $response['number'] = $number;

        $response['error'] = false;
    } else {
        $response['error'] = true;
        $response['message'] = "No data found";
    }
    $stmt->close();
} else {
    $response['error'] = true;
    $response['message'] = "Invalid request. Username not provided.";
}

echo json_encode($response);



?>