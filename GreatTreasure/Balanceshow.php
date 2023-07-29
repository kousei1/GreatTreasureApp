<?php
require 'connection.php';
$response = array();

if (isset($_POST['uid'])) {
    $uid = $_POST['uid'];

    $stmt = $conn->prepare("SELECT uid, balance FROM userwallettbl WHERE uid = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($uid, $balance);
        $stmt->fetch();
        $response['uid'] = $uid;
        $response['balance'] = $balance;
        $response['error'] = false;
    } else {
        $response['error'] = true;
        $response['message'] = "No balance found for the user.";
    }

    $stmt->close();
} else {
    $response['error'] = true;
    $response['message'] = "Invalid request. Username not provided.";
}
echo json_encode($response);
?>
