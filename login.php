<?php
include("config.php");

global $dbconn

function confirm_query($result_set) {
	    if (!$result_set) {
		    die("Database query failed.");
    	    }
   }

//$sql = "SELECT * FROM Admin where Username = '" . $_GET['uname'] ."' AND Password = '" . $_GET['pword'] . "'";
$query = "SELECT * FROM student_account where student_ua_email = '" . $_POST['username'] ."' AND student_ua_password = '" . $_POST['password'] . "'";

$result = pg_query($dbconn,$query)

  
$no_of_rows = mysql_num_rows($result);
	if ($no_of_rows > 0) {
		$response["success"] = 1;
		$response["username"] = $result[1];
		$response["password"] = $result[2];
		$response[$result[2]];
		$response[$result[3]];
		$response[$result[0]];

	}
	else
	{
		$response["success"] = 0;
		$response["error"] = "Username or password incorrect";
	}
  echo json_encode($response);

mysql_close($con);
?> 