<?php
require 'connection.php';


$response = array();


if(isset($_POST['number']) && isset($_POST['moneysend'])){
//send money
$num = $_POST['number'];
$sendmoney = $_POST['moneysend'];
$sendID = $_POST['sendID'];


      // Get the current balance from the userwallettbl
      $stmtBalance = $conn->prepare("SELECT balance - ? FROM userwallettbl WHERE uid = ?");
      $stmtBalance->bind_param("si", $sendmoney, $sendID);
      $stmtBalance->execute();
      $stmtBalance->bind_result($currentBalance);
      $stmtBalance->fetch();
      
      $stmtBalance->close();


      $stmtbalanceNum = $conn->prepare("SELECT b.balance + ? FROM userwallettbl AS b INNER JOIN usertbl AS u ON b.uid = u.uid WHERE u.number = ?");
      $stmtbalanceNum->bind_param("ss", $sendmoney, $num);
      $stmtbalanceNum->execute();
      $stmtbalanceNum->bind_result($numBalanceResult);
      $stmtbalanceNum->fetch();

      $stmtbalanceNum->close();
 
     if ($currentBalance >= 500) {

        if ($numBalanceResult >= 50000)
        {
            $response['error'] = true;
            $response['message'] = "The number sending money reach maximum";
        }else{
            $response['error'] = false;
      
            $stmt1 = $conn->prepare("UPDATE userwallettbl AS wallet INNER JOIN usertbl AS user ON  user.uid = wallet.uid SET wallet.balance = wallet.balance + ? WHERE user.number = ?");
            $stmt1->bind_param("ss", $sendmoney, $num);
            $stmt1->execute();
    
            if($stmt1->affected_rows > 0)
            {
                $response['error'] = false;
    
                $stmt2 = $conn->prepare("UPDATE userwallettbl SET balance = balance - ? WHERE uid = ?");
                $stmt2->bind_param("si", $sendmoney, $sendID);
                $stmt2->execute();
    
                $status = "Send Money";
                date_default_timezone_set("Asia/Manila");
                $date = date("d/m/Y h:i:a");
    
                $stmt3 = $conn->prepare("INSERT INTO transaction (uid, Trans_Date, Trans_Total, Trans_Status) VALUES (?, ?, ?, ?)");
                $stmt3->bind_param("isss", $sendID, $date, $sendmoney, $status);
                $stmt3->execute();
    
    
                $transID = $stmt3->insert_id;//retrieve last inserted
                $stmt4 = $conn->prepare("INSERT INTO historytransaction (`Trans_ID`, `uid`, `Date`, `trans_types`) VALUES (?, ?, ?, ?)");
                $stmt4->bind_param("iiss", $transID, $sendID, $date, $status);
                $stmt4->execute();
    
                $response['message'] = "Successfully sended";
    
            }else
            {
                $response['error'] = true;
                $response['message'] = "This number isn't registered";
            }

        }

     

     }else
     {
        $response['error'] = true;
        $response['message'] = "you exceed the maintain balance";
     }

}else if(isset($_POST['number']) && isset($_POST['AmountIn'])){
    //cash in
    $number = $_POST['number'];
    $amount = $_POST['AmountIn'];
    // Get the current balance from the userwallettbl
    $stmtBalance1 = $conn->prepare("SELECT wallet.balance + ? FROM userwallettbl wallet INNER JOIN usertbl u ON wallet.uid = u.uid WHERE u.number = ?");
    $stmtBalance1->bind_param("ss", $amount, $number);
    $stmtBalance1->execute();
    $stmtBalance1->bind_result($currentBalance);
    $stmtBalance1->fetch();

    $stmtBalance1->close();

    if ($currentBalance > 50000)
    {
        $response['error'] = true;
        $response['message'] = "This user reach the maximum balance";
    }else{
        $response['error'] = false;
        
    $number = $_POST['number'];
    $amount = $_POST['AmountIn'];

    $stmt5 = $conn->prepare("UPDATE userwallettbl AS wallet INNER JOIN usertbl AS user ON wallet.uid = user.uid SET wallet.balance =  wallet.balance + ? WHERE user.number = ?");
    $stmt5->bind_param("ss", $amount, $number);
    $stmt5->execute();


  

    if($stmt5->affected_rows > 0)
    {
        $response['error'] = false;


        $stmtnumber = $conn->prepare("SELECT uid FROM usertbl WHERE number = ?");
        $stmtnumber->bind_param("s", $number);
        $stmtnumber->execute();
        $stmtnumber->bind_result($currentNumberUid);
        $stmtnumber->fetch();
        
        $stmtnumber->close();


        date_default_timezone_set("Asia/Manila");
        $dateload = date("d/m/Y h:i:a");
        $loadstatus = "Cash In";

        $stmt6 = $conn->prepare("INSERT INTO transaction (uid, Trans_Date, Trans_Total, Trans_Status) VALUES (?, ?, ?, ?)");
        $stmt6->bind_param("isss", $currentNumberUid, $dateload, $amount, $loadstatus);
        $stmt6->execute();


        $loadTrans = $stmt6->insert_id;//retrieve last inserted
        $stmt7 = $conn->prepare("INSERT INTO historytransaction (`Trans_ID`, `uid`, `Date`, `trans_types`) VALUES (?, ?, ?, ?)");
        $stmt7->bind_param("iiss", $loadTrans, $currentNumberUid, $dateload, $loadstatus);
        $stmt7->execute();

        $response['message'] = $stmt7;
    }else{
        $response['error'] = true;
        $response['message'] = "This number isn't registered";
    }
    }



}else
{
    $response['error'] = true;
    $response['message'] = "Invalid request parameters";
}



echo json_encode($response);




?>