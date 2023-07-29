<?php
require 'connection.php';
$response = array();

if (isset($_POST['uid']) && isset($_POST['number']) && isset($_POST['name']) && isset($_POST['balancereq'])) {
    $uid = $_POST['uid'];
    $number = $_POST['number'];
    $name = $_POST['name'];
    $balreq = $_POST['balancereq'];

    $stmt = $conn->prepare("SELECT number FROM usertbl WHERE number = ?");
    $stmt->bind_param("s", $number);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $response['error'] = false;

        date_default_timezone_set("Asia/Manila");
        $date = date("d/m/Y h:i:a");
        $status = "Pending";
        $stmt2 = $conn->prepare("INSERT INTO userrequesttbl (uid, BalanceReq, date, StatusReq, nameReqTo, numberReqTo) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt2->bind_param("idssss", $uid, $balreq, $date, $status, $name, $number);

        if ($stmt2->execute() == TRUE)
        {
            $response['error'] = false;
            $response['message'] = "Success send request";
        }else
        {
            $response['error'] = true;
            $response['message'] = "Failed to send request";
        }



    } else {
        $response['error'] = true;
        $response['message'] = "This number isn't registered yet";
    }

    $stmt->close();
} else {
    $response['error'] = true;
    $response['message'] = "Invalid request. Username not provided.";
}

echo json_encode($response);



?>