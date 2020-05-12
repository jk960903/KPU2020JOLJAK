<?php

$con = mysqli_connect("localhost", "root","","Patient");

if(!$con)
{
	echo "Failed to connection to MySQL : ";
	echo mysqli_connect_error();
	exit();
}

mysqli_set_charset($con, "utf8");

$res = mysqli_query($con, "select * from Patient");
$result = array();

while($row = mysqli_fetch_array($res)){
array_push($result, array('no'=>$row[0], 'name'=>$row[3], 'p_id'=>$row[1],'p_pw'=>$row[2]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);
?>