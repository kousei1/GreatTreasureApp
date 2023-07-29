<?php
require 'connection.php';
$response = array();

if (isset($_POST['uid'])) {
    $uid = $_POST['uid'];
    

    $searchtxt = isset($_POST['searchview']) ? $_POST['searchview'] : null;

    if ($searchtxt !== null) {
        $stmt5 = $conn->prepare("SELECT h.transh_ID, h.trans_ID, h.Date, h.trans_types, h.uid, t.Trans_Total FROM historytransaction AS h INNER JOIN transaction AS t ON h.trans_ID = t.trans_ID WHERE h.uid = ? AND (h.Date LIKE CONCAT('%', ?, '%') OR h.trans_types LIKE CONCAT('%', ?, '%'))");
        $stmt5->bind_param("iss", $uid, $searchtxt, $searchtxt);
        $stmt5->execute();
        $result2 = $stmt5->get_result();

        if ($result2->num_rows > 0) {
            $response['data'] = array(); // Initialize the data array

            while ($row = $result2->fetch_assoc()) {
                $data = array(
                    'transh_ID' => $row['transh_ID'],
                    'trans_ID' => $row['trans_ID'],
                    'Date' => $row['Date'],
                    'trans_types' => $row['trans_types'],
                    'uid' => $row['uid'],
                    'Trans_Total' => $row['Trans_Total']
                );
                array_push($response['data'], $data); // Add each data object to the array
            }

            $response['error'] = false;
        } else {
            $response['error'] = true;
            $response['message'] = "Not found";
        }
    } else {
        $startDate = $_POST['startDate'];
        $stmt = $conn->prepare("SELECT h.transh_ID, h.trans_ID, h.Date, h.trans_types, h.uid, t.Trans_Total FROM historytransaction AS h INNER JOIN transaction AS t ON h.trans_ID = t.trans_ID WHERE h.uid = ? AND h.Date LIKE CONCAT('%', ?, '%') ORDER BY h.trans_ID DESC");
        $stmt->bind_param("is", $uid, $startDate);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $response['data'] = array(); // Initialize the data array

            while ($row = $result->fetch_assoc()) {
                $data = array(
                    'transh_ID' => $row['transh_ID'],
                    'trans_ID' => $row['trans_ID'],
                    'Date' => $row['Date'],
                    'trans_types' => $row['trans_types'],
                    'uid' => $row['uid'],
                    'Trans_Total' => $row['Trans_Total']
                );
                array_push($response['data'], $data); // Add each data object to the array
            }

            $response['error'] = false;
        } else {
            // Check if any data exists for the user, regardless of the date range
            $stmt4 = $conn->prepare("SELECT h.transh_ID, h.trans_ID, h.Date, h.trans_types, h.uid, t.Trans_Total FROM historytransaction AS h INNER JOIN transaction AS t ON h.trans_ID = t.trans_ID WHERE h.uid = ? ORDER BY h.trans_ID DESC");
            $stmt4->bind_param("i", $uid);
            $stmt4->execute();
            $result1 = $stmt4->get_result();

            if ($result1->num_rows > 0) {
                $response['data'] = array(); // Initialize the data array

                while ($row = $result1->fetch_assoc()) {
                    $data = array(
                        'transh_ID' => $row['transh_ID'],
                        'trans_ID' => $row['trans_ID'],
                        'Date' => $row['Date'],
                        'trans_types' => $row['trans_types'],
                        'uid' => $row['uid'],
                        'Trans_Total' => $row['Trans_Total']
                    );
                    array_push($response['data'], $data); // Add each data object to the array
                }

                $response['error'] = false;
            } else {
                $response['error'] = true;
                $response['message'] = "No data found for the user.";
            }
            $response['error'] = true;
            $response['message'] = "No record on this day";
        }
    }
}else if(isset($_POST['transh_ID']))
{

    $t_id = $_POST['transh_ID'];

    $stmt2 = $conn->prepare("DELETE FROM historytransaction WHERE transh_ID = ?");
    $stmt2->bind_param("i", $t_id);

    if ($stmt2->execute()) {
        $response['error'] = false;
        $response['message'] = "Data Deleted Successfully!";
    } else {
        $response['error'] = true;
        $response['message'] = "Deletion Failed.";
    }
}else if(isset($_POST['historyID'])){

    $hID = $_POST['historyID'];
    $stmt3 = $conn->prepare("SELECT trans_ID, Trans_Date, Trans_Total, Trans_Status FROM transaction WHERE trans_ID = ?");
    $stmt3->bind_param("i", $hID);
    $stmt3->execute();
    $stmt3->store_result();

    if ($stmt3->num_rows > 0) {
        $stmt3->bind_result($transID, $Date, $transTotal, $transType);
        $stmt3->fetch();

        $response['trans_ID'] = $transID;
        $response['Trans_Date'] = $Date;
        $response['Trans_Total'] = $transTotal;
        $response['Trans_Status'] = $transType;
        $response['error'] = false;
        $response['message'] = "";
    } else {
        $response['error'] = true;
        $response['message'] = "No Data Found";
    }


}else 
{
    $response['error'] = true;
    $response['message'] = "Insufficient Parameters.";
}
    
echo json_encode($response);
?>
