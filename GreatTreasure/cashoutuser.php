<?php
require 'connection.php';

$response = array();

if (isset($_POST['uid']) && isset($_POST['balancewithdraw'])) {
    $uid = $_POST['uid'];
    $cashout = $_POST['balancewithdraw'];


     // Get the current balance from the userwallettbl
     $stmtBalance = $conn->prepare("SELECT balance - ? FROM userwallettbl WHERE uid = ?");
     $stmtBalance->bind_param("si", $cashout, $uid);
     $stmtBalance->execute();
     $stmtBalance->bind_result($currentBalance);
     $stmtBalance->fetch();
     
     $stmtBalance->close();

     if ($currentBalance >= 500) {

            $response['error'] = false;


        $stmt = $conn->prepare("UPDATE userwallettbl SET balance = balance - ? WHERE uid = ?");
        $stmt->bind_param("sd", $cashout, $uid);
        $stmt->execute();

        if ($stmt->affected_rows  > 0) {
            $response['error'] = false;
            $transStatus = "CashOut";

            date_default_timezone_set("Asia/Manila");
            $date = date("d/m/Y h:i:a");
            $stmt2 = $conn->prepare("INSERT INTO transaction (uid, Trans_Date, Trans_Total, Trans_Status) VALUES(?, ?, ?, ?)");
            $stmt2->bind_param("isss", $uid, $date, $cashout, $transStatus);
            $stmt2->execute();

            $transID = $stmt2->insert_id; // Retrieve last inserted ID

            $stmt3 = $conn->prepare("INSERT INTO historytransaction (Trans_ID, uid, Date, trans_types) VALUES (?, ?, ?, ?)");
            $stmt3->bind_param("iiss", $transID, $uid, $date, $transStatus);
            $stmt3->execute();
            

            $response['message'] = "Successfully withdraw balance";


        
        } else {
            $response['error'] = true;
            $response['message'] = "Failed to withdraw";
        }

    $stmt->close();
    $stmt2->close();
    $stmt3->close();
     }
     else
     {
        $response['error'] = true;
        $response['message'] = "You reach your maintained balance";
     }
    
   
} else {
    $response['error'] = true;
    $response['message'] = "Invalid request.";
}

echo json_encode($response);



?>