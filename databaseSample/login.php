<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    $username = $_POST['username'] ?? ''; // Use null coalescing to avoid undefined index notice
    $password = $_POST['password'] ?? '';

    require_once 'connection.php';

    $sql = "SELECT * FROM tableuser WHERE username = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $response = $stmt->get_result();

    $result = array();
    $result['login'] = array();

    if (mysqli_num_rows($response) === 1) {
        $row = mysqli_fetch_assoc($response);
        if (password_verify($password, $row['password'])) {
            $index = array();
            $index['userid'] = $row['userid'];
            $index['username'] = $row['username'];

            array_push($result['login'], $index);

            $result["success"] = "1";
            $result["message"] = "Login successful";
        } else {
            $result["success"] = "0";
            $result["message"] = "Invalid password";
        }
    } else {
        $result["success"] = "0";
        $result["message"] = "User not found";
    }

    echo json_encode($result);
    
   
    mysqli_close($conn);
}
?>
