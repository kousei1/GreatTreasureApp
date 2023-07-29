<?php


require 'connection.php';
$response = array();

if (isset($_POST['uid'])) {
    $uid = $_POST['uid'];

    $stmt = $conn->prepare("SELECT uw.uid, uw.balance, u.number FROM userwallettbl uw INNER JOIN usertbl u ON uw.uid = u.uid WHERE uw.uid = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($uid, $balance, $number);
        $stmt->fetch();
        $response['uid'] = $uid;
        $response['balance'] = $balance;
        $response['number'] = $number;
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








// require 'connection.php';
// $response = array();
// if(isset($_POST['acceptRedID'])) {

//     $acceptID = $_POST['acceptRedID'];
//     $userBalance = $_POST['balance'];
//     $stat = "Accepted";
//     $stmt2 = $conn->prepare("UPDATE userrequesttbl user INNER JOIN userwallettbl wallet ON  user.reqID=wallet.uid SET user.StatusReq = ?, wallet.balance = ?,
//     wallet.balance = (wallet.balance - user.BalanceReq) WHERE user.uid = ?");
//     $stmt2->bind_param("ssi", $stat, $userBalance, $acceptID);
//     $stmt2->execute();
//     if ($stmt2->affected_rows > 0)
//     {
//         $response['error'] = false;
//         date_default_timezone_set("Asia/Manila");
//         $date = date("d/m/y h:ia");
//         $transStats = "Approved";
//         $stmt2 = $conn->prepare("INSERT INTO transaction (uid, Trans_Date, trans_Total, trans_Status)
//         VALUES (?, ?, (SELECT user.BalanceReq, wallet.balance (wallet.balance - user.BalanceReq) FROM userrequesttbl AS user INNER JOIN userwallettbl AS wallet ON user.reqID = wallet.uid), ?)");
//         $stmt2->bind_param("iss", $acceptID, $date, $transStats);
//         $stmt2->execute();

//         if ($stmt2->affected_rows > 0) {
//             // Insertion successful
//             $response['error'] = false;
            

//             $transID = $stmt2->insert_id;
//             $typestrans = "Approved Req";
//             $stmt3 = $conn->prepare("INSERT INTO historytransaction (Trans_ID, uid, Date, trans_types) VALUES (?, ?, ?, ?)");
//             $stmt3->bind_param("iiss", $transID, $uid, $date, $typestrans);
//             if ($stmt3->affected_rows > 0) {
//                 // Insertion into historytransaction successful
//                 $response['message'] = "Request approved and transaction recorded.";
//             } else {
//                 // Failed to insert into historytransaction
//                 $response['message'] = "Failed to record the transaction in history.";
//             }



//         } else {
//             // Failed to insert transaction
//             $response['message'] = "Failed to record the transaction.";
//         }

//         $stmt2->close();
//     } else {
//         // Failed to update the request status
//         $response['error'] = true;
//         $response['message'] = "Failed to update the request status.";
//     }

//     $stmt2->close();


//     } else {
//     $response['error'] = true;
//     $response['message'] = "Insufficient parameters.";
// }

// echo json_encode($response);

?>