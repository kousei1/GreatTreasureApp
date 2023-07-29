<?php
require 'connection.php';

$response = array();

if(isset($_POST['num'])) {
    $num = $_POST['num'];
    $stats = "Pending";

    $searchNum = isset($_POST['searchReq']) ? $_POST['searchReq'] : null;

    if ($searchNum !== null)
    {
      
        $response['error'] = false;
        $stmt5 = $conn->prepare("SELECT r.reqID, r.uid, r.BalanceReq, r.date, u.number FROM userrequesttbl r RIGHT JOIN usertbl u ON r.uid = u.uid WHERE r.numberReqTo = ? AND u.number LIKE CONCAT('%', ?, '%') AND StatusReq = ? ORDER BY r.date DESC");
        $stmt5->bind_param("sss", $num,$searchNum, $stats);
        $stmt5->execute();
        $result = $stmt5->get_result();
    
        if ($result->num_rows > 0) {
            $response['data'] = array(); // Initialize the data array
    
            while ($row = $result->fetch_assoc()) {
                $data = array(
                    'date' => $row['date'],
                    'BalanceReq' => $row['BalanceReq'],
                    'number' => $row['number'],
                    'reqID' => $row['reqID'],
                    'uid' => $row['uid']
                );
                array_push($response['data'], $data); // Add each data object to the array
            }
    
        } else {
            $response['error'] = true;
            $response['message'] = "Invalid search";
            
        }
        $stmt5->close();
       

    }else
    {
        $num = $_POST['num'];
        $stmt = $conn->prepare("SELECT r.reqID, r.uid, r.BalanceReq, r.date, u.number FROM userrequesttbl r RIGHT JOIN usertbl u ON r.uid = u.uid WHERE r.numberReqTo = ? AND StatusReq = ? ORDER BY r.date DESC");
        $stmt->bind_param("ss", $num, $stats);
        $stmt->execute();
        $result = $stmt->get_result();
    
        if ($result->num_rows > 0) {
            $response['data'] = array(); // Initialize the data array
    
            while ($row = $result->fetch_assoc()) {
                $data = array(
                    'date' => $row['date'],
                    'BalanceReq' => $row['BalanceReq'],
                    'number' => $row['number'],
                    'reqID' => $row['reqID'],
                    'uid' => $row['uid']
                );
                array_push($response['data'], $data); // Add each data object to the array
            }
    
            $response['error'] = false;
        } else {
            $response['error'] = true;
            $response['message'] = "No data found for the user.";
        }
        $stmt->close();
    }

   
}else if(isset($_POST['uidReq'])){

    $declinedStatus = "Declined";
    $declineNum = $_POST['uidReq'];
    $stmt1 = $conn->prepare("UPDATE userrequesttbl SET StatusReq = ? WHERE reqID = ?");
    $stmt1->bind_param("si", $declinedStatus, $declineNum);
    $stmt1->execute();

    if ($stmt1->affected_rows > 0)
    {
        $response['error'] = false;
        
    }else
    {
        $response['error'] = true;
        $response['message'] = "Failed to declined";
    }


} else if (isset($_POST['acceptReqID'])) {
    $acceptID = $_POST['acceptReqID'];
    $uid = $_POST['IDUID'];
    $stat = "Accepted";
    $cashout = $_POST['userbalance'];
    
    // Get the current balance from the userwallettbl
    $stmtBalanceNum = $conn->prepare("SELECT b.balance + ? FROM userwallettbl AS b INNER JOIN userrequesttbl AS r ON b.uid = r.uid WHERE r.reqID = ?");
    $stmtBalanceNum->bind_param("si", $cashout, $acceptID);
    $stmtBalanceNum->execute();
    $stmtBalanceNum->bind_result($resultBalanceNum);
    $stmtBalanceNum->fetch();

    $stmtBalanceNum->close();
    
        if ($resultBalanceNum >= 50000) {
            $response['error'] = true;
            $response['message'] = "This number reach the maximum balance.";
        } else {

            $stmtBalance = $conn->prepare("SELECT balance - ? FROM userwallettbl WHERE uid = ?");
            $stmtBalance->bind_param("di", $cashout, $uid);
            $stmtBalance->execute();
            $stmtBalance->bind_result($resultBalance);
            $stmtBalance->fetch();

            $stmtBalance->close();
                
                if ($resultBalance >= 500) {

                    $stmt2 = $conn->prepare("UPDATE userrequesttbl AS user
                        INNER JOIN usertbl AS r ON user.numberReqTo = r.number
                        INNER JOIN userwallettbl AS wallet ON r.uid = wallet.uid
                        SET user.StatusReq = ?, wallet.balance = wallet.balance - user.BalanceReq WHERE user.numberReqTo = r.number AND user.reqID = ?");
                    $stmt2->bind_param("si", $stat, $acceptID);
                    $stmt2->execute();
                    
                    $stmt5 = $conn->prepare("UPDATE userrequesttbl AS user INNER JOIN userwallettbl AS wallet ON user.uid = wallet.uid
                        SET wallet.balance = wallet.balance + user.BalanceReq WHERE user.reqID = ?");
                    $stmt5->bind_param("i", $acceptID);
                    $stmt5->execute();
                    
                    if ($stmt2->affected_rows > 0) {
                        date_default_timezone_set("Asia/Manila");
                        $date = date("d/m/Y h:i:a");
                        $transStats = "Approved";
                        
                        $stmt3 = $conn->prepare("INSERT INTO transaction (uid, Trans_Date, trans_Total, trans_Status)
                            SELECT ?, ?, user.BalanceReq, ?
                            FROM userrequesttbl AS user
                            INNER JOIN userwallettbl AS wallet ON user.uid = wallet.uid
                            WHERE user.reqID = ?");
                        $stmt3->bind_param("issi", $uid, $date, $transStats, $acceptID);
                        $stmt3->execute();
                        
                        if ($stmt3->affected_rows > 0) {
                            $transID = $stmt3->insert_id;
                            $typestrans = "Loan";
                            
                            $stmt4 = $conn->prepare("INSERT INTO historytransaction (`Trans_ID`, `uid`, `Date`, `trans_types`) VALUES (?, ?, ?, ?)");
                            $stmt4->bind_param("iiss", $transID, $uid, $date, $typestrans);
                            
                            if ($stmt4->execute()) {
                                $response['error'] = false;
                                $response['message'] = "Request approved and transaction recorded.";
                            } else {
                                $response['error'] = true;
                                $response['message'] = "Failed to record the transaction in history.";
                            }
                            
                            $stmt4->close();
                        } else {
                            $response['error'] = true;
                            $response['message'] = "Failed to record the transaction.";
                        }
                        
                        $stmt2->close();
                        $stmt3->close();
                    } else {
                        $response['error'] = true;
                        $response['message'] = "Failed to update the request status.";
                    }
                } else {
                    $response['error'] = true;
                    $response['message'] = "You've reached the minimum balance.";
                }
            
        }

} else {
    $response['error'] = true;
    $response['message'] = "Missing required parameters.";
}

// Return the response as JSON
echo json_encode($response);


?>
