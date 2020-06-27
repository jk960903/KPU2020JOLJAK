<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');

 

//POST 값을 읽어온다.
$walk=isset($_POST['walk']) ? $_POST['walk'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($walk != "" ){ 

    $sql="select * from distance where walk order by datetime";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $walk;
        echo "'은 찾을 수 없습니다.";
    }
	else{

   		$data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

            array_push($data, 
                  array('p_id'=>$row["p_id"],
                'walk'=>$row["walk"],
                'datetime'=>$row["datetime"]
            ));
        }


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }
}
else {
    echo "검색할 나라를 입력하세요 ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         나라 이름: <input type = "text" name = "country" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>