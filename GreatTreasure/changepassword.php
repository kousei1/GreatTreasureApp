<?php
require 'connection.php';
$response = array();

if(isset($_POST['email']) && isset($_POST['fpassword']))
{
    $email = $_POST['email'];
    $fpass = $_POST['fpassword'];

 
        $comm = $conn->prepare("UPDATE usertbl SET password = ? WHERE email = ? OR number = ? OR username = ?");
        $comm->bind_param("ssss", $fpass, $email, $email, $email);
        $comm->execute();

       
            if($comm->affected_rows > 0)
            {
                $response['error'] = false;
                $response['message'] = "Successfully recovered";
            }else
            {
                $response['error'] = true;
                $response['message'] = "Email not found";
            }

}
//change password
else if (isset($_POST['cpassword']) && isset($_POST['password']))
{

    $password = $_POST['password'];
    $current = $_POST['cpassword'];
    $uid = $_POST['uid'];


    $comm2 = $conn->prepare("SELECT password FROM usertbl WHERE uid = ? AND password = ?");
    $comm2->bind_param("is", $uid, $current);
    $comm2->execute();
    $comm2->store_result();
    if($comm2->num_rows > 0)
    {
        $response['error'] = false;
        $comm3 = $conn->prepare("UPDATE usertbl SET password = ? WHERE uid = ? ");
        $comm3->bind_param("si", $password, $uid);

        if ($comm3->execute() == TRUE)
        {

            $response['error'] = false;
            $response['message'] = "Successfully change password";
        }else
        {
            $response['error'] = true;
            $response['message'] = "Failed to change password";
        }


    }else
    {
        $response['error'] = true;
        $response['message'] = "Password doesn't match to the current password";
    }
    $comm2->close();
}else 
{
    $response['error'] = true;
    $response['message'] = "Insufficient parameters.";
}

echo json_encode($response);

?>