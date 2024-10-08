<?php
$token = "API Key";//토큰 없어도 작동됨
$options = [
    'http' => [
        'header' => "accept: application/json\r\n" . "x-nxopen-api-key: $token",
        'method' => 'GET',
        'timeout' => 10,
    ],
];


/**
 * 라이더 닉네임을 ouid로 반환
 */
function getouid(string $racername): string {
    global $options, $api;
    $http = "https://open.api.nexon.com/kartrush/v1/id";

    $url = "$http?racer_name=" . urlencode($racername);
    $context = stream_context_create($options);
    $response = @file_get_contents($url, false, $context);
    $data = json_decode($response, true);
    if (isset($data['ouid_info'][0]['ouid'])) {
        return $data['ouid_info'][0]['ouid'];
    } else {
        return "(Unknown)";
    }
}

/**
 * 라이더의 정보르를 json로 반환/불러옴
 */

function getJson(string $ouid)
{
    global $options;
    $ouid = getouid($ouid);
    $url = 'https://open.api.nexon.com/kartrush/v1/user/basic?ouid=' . urlencode($ouid);
    $context = stream_context_create($options);
    $response = @file_get_contents($url, false, $context);
    $data = json_decode($response, true);
    return $data;
}

/**
 * 해당 라이더의 식별자를 불러옴
 */
function getRacername(string $ouid): string {
    $json = getJson($ouid);
    return $json["racer_name"] ?? "(Unknown)";
}

/**
 * 현재 날짜를 한국식으로 반환
 */
function getTime(string $date){
    $datetime = new DateTime($date);
    $a = ($datetime->format("A") == "PM") ? '오후' : '오전';
    $time = $datetime->format("Y년 m월 d일 $a h시 i분 s초");
    return $time;
}

/**
 * 해당 라이더의 계정을 처음 만든 날짜를 불러옴
 *
 * bool = true일경우 한국식으로 반환 그렇지 않을경우 카러플식으로 반환
 */


function createdate(string $ouid, bool $bool = false): string
{
    $json = getJson($ouid);
    $time = $json["racer_date_create"] ?? false;
    if ($bool){
        $time = getTime($time);
    }
    return $time;
}

/**
 * 라이더의 마지막 로그인을 불러옴
 *
 * bool = true일경우 한국식으로 반환 그렇지 않을경우 카러플식으로 반환
 */

function getLastLogin(string $ouid, bool $bool = false): string{
    $json = getJson($ouid);
    $time = $json["racer_date_last_login"] ?? false;
    if ($bool){
        $time = getTime($time);
    }
    return $time;
}

/**
 * 러아더가 로그아웃을 언제하는지 출력
 *
 * bool = true일경우 한국식으로 반환 그렇지 않을경우 카러플식으로 반환
 */

function getLastLogout(string $ouid, bool $bool = false): string
{
    $json = getJson($ouid);
    $time = $json["racer_date_last_logout"] ?? false;
    if ($bool){
        $time = getTime($time);
    }
    return $time;
}

/**
 * 라이더 레벨을 가져옴
 */

function getLevel(string $ouid): string
{
    $json = getJson($ouid);
    return $json["racer_level"] ?? "(Unknown)";
}

/**
 * 타이틀을 가져옴
 */

function getTitle(string $ouid){
    global $options;
    $ouid = getouid($ouid);
    $context = stream_context_create($options);
    $url = 'https://open.api.nexon.com/kartrush/v1/user/title-equipment';
    $urls = "$url?ouid=" .urlencode($ouid);
    $response = file_get_contents($urls, false, $context);
    $titlea = json_decode($response, true);
    $title = $titlea["title_equipment"][0]["title_name"] ?? "없음";
    return $title;
}

/**
 * getDatediff()
 * $min, $max경우
 * $min: 계산 하라는 날짜
 * $max: 계산 하라는 날짜
 */

function getDatediff($min, $max)
{
    $dateMin = new DateTime($min);
    $dateMax = new DateTime($max);
    $interval = $dateMin->diff($dateMax);

    $years = $interval->y;
    $months = $interval->m;
    $days = $interval->d;
    $hours = $interval->h;
    $minutes = $interval->i;
    $seconds = $interval->s;

    $formatted = '';

    if ($years > 0) {
        $formatted .= $years . '년 ';
    }
    if ($months > 0) {
        $formatted .= $months . '개월 ';
    }
    if ($days > 0) {
        $formatted .= $days . '일 ';
    }
    if ($hours > 0) {
        $formatted .= $hours . '시간 ';
    }
    if ($minutes > 0) {
        $formatted .= $minutes . '분 ';
    }
    if ($seconds > 0 || $formatted === '') {
        $formatted .= $seconds . '초';
    }

    return $formatted;
}


/**
 * 공지 관련
 */


$url = "https://openapi.nexon.com/ko/game/kartrush/v1/notice";

function getNoticeJson(): array{
    global $options;
    $list = [];
    $url = 'https://open.api.nexon.com/kartrush/v1/notice';
    $context = stream_context_create($options);
    $response = @file_get_contents($url, false, $context);
    $data = json_decode($response, true);
    foreach ($data["notice"] as $v) {
        $list[] = $v;
    }
    return $list;
}

/**
 * 모든 공지의 제목을 불러옵니다.(최대 20까지)
 */

function getTitleNotice(int $count = 20){
    $notice = getNoticeJson();
    $list = [];
    $a = 0;
    foreach ($notice as $v) {
        $a ++ ;
        if ($a <= $count) {
            $list[] = $v["title"];
        }
    }
    return $list;
}

/**
 * 공지 고유번호
 */

function getNoticeid(int $count = 20)
{
    $notice = getNoticeJson();
    $list = [];
    $a = 0;
    foreach ($notice as $v) {
        $a ++ ;
        if ($a <= $count) {
            $list[] = $v["notice_id"];
        }
    }
    return $list;
}

/**
 * 공지 고유번호
 */

function getNoticeDate(int $count = 20, bool $bool = false)
{
    $notice = getNoticeJson();
    $list = [];
    $a = 0;
    foreach ($notice as $v) {
        $a ++ ;
        if ($a <= $count) {
            $time = $v["date"];
            if ($bool){
                $time = getTime($time);
            }
            $list[] = $time;
        }
    }
    return $list;
}

/**
 * 공지 고유번호
 */

function getNoticeUrl(int $count = 20)
{
    $notice = getNoticeJson();
    $list = [];
    $a = 0;
    foreach ($notice as $v) {
        $a ++ ;
        if ($a <= $count) {
            $list[] = '<a href="' . htmlspecialchars($v["url"]) . '" target="_blank">' . htmlspecialchars($v["url"]) . '</a>';
        }
    }
    return $list;
}

/**
 * 공지 상세정보
 */

function geetNoticeinfo(int $noticeid)
{
    /**
     * 제작중
     */
}

?>
