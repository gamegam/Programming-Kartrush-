import androidx.compose.material3.LocalTonalElevationEnabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class KartAPI {

    suspend fun APIKey(): String {
        return "API 토큰 키 "
    }

    //ouid(계정 정보)을 불러오는 매소드

    suspend fun getOuid(racerName: String): String {
        val apiUrl = "https://open.api.nexon.com/kartrush/v1/id"
        val apiKey = APIKey()
        return try {
            val encodedRacerName = URLEncoder.encode(racerName, "UTF-8")
            val urlString = "$apiUrl?racer_name=$encodedRacerName"
            val url = URL(urlString)
            withContext(Dispatchers.IO) {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("accept", "application/json")
                connection.setRequestProperty("x-nxopen-api-key", apiKey)
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    reader.use { it.forEachLine { line -> response.append(line) } }
                    val jsonResponse = JSONObject(response.toString())
                    val ouidInfo = jsonResponse.optJSONArray("ouid_info")
                    if (ouidInfo != null && ouidInfo.length() > 0) {
                        val ouidObject = ouidInfo.getJSONObject(0)
                        return@withContext ouidObject.optString("ouid", "(Unknown)")
                    } else {
                        return@withContext "(Unknown)"
                    }
                } else {
                    return@withContext "응답코드: " + responseCode.toString()
                }
            }
        } catch (e: Exception) {
            "100"
        }
    }

    /**
     * 캐릭터의 정보(type)을 불러오는 소스 입니다.
     * 예시 getStten("ouid", "create_date") //계정 가입 날짜를 불러옴
     */

    suspend fun getStten(ouid: String, type: String): String {
        val apiUrl = "https://open.api.nexon.com/kartrush/v1/user/basic"
        val apiKey = APIKey()
        val ouid = getOuid(ouid)
        return try {
            val encodedOuid = URLEncoder.encode(ouid, "UTF-8")
            val urlString = "$apiUrl?ouid=$encodedOuid"
            val url = URL(urlString)
            withContext(Dispatchers.IO) {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("accept", "application/json")
                connection.setRequestProperty("x-nxopen-api-key", apiKey)
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    reader.use { it.forEachLine { line -> response.append(line) } }
                    val jsonResponse = JSONObject(response.toString())
                    jsonResponse.optString(type, "(Unknown)")//racer_level
                } else {
                    "null"
                }
            }
        } catch (e: Exception) {
            "-100"
        }
    }

    suspend fun getTitle(ouid: String): String {
        val apiUrl = "https://open.api.nexon.com/kartrush/v1/user/title-equipment"
        val apiKey = APIKey()
        //val ouidEncoded = URLEncoder.encode(ouid, "UTF-8")
        val ouidEncoded = getOuid(ouid)
        return try {
            val urlString = "$apiUrl?ouid=$ouidEncoded"
            val url = URL(urlString)

            withContext(Dispatchers.IO) {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("accept", "application/json")
                connection.setRequestProperty("x-nxopen-api-key", apiKey)

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    reader.use { it.forEachLine { line -> response.append(line) } }

                    val jsonResponse = JSONObject(response.toString())
                    val titleEquipmentArray = jsonResponse.getJSONArray("title_equipment")
                    if (titleEquipmentArray.length() > 0) {
                        val firstTitleEquipment = titleEquipmentArray.getJSONObject(0)
                        firstTitleEquipment.optString("title_name", "(알 수 없음)\n개발자에게 연락하세요!")
                    } else {
                        "타이틀 없음"
                    }
                } else {
                    "Error: Response code $responseCode"
                }
            }
        } catch (e: Exception) {
            "-100"
        }
    }

    //계정 생성일
    suspend fun createdate(ouid: String, bool: Boolean = false): String {
        val apiUrl = "https://open.api.nexon.com/kartrush/v1/user/basic"
        val apiKey = APIKey()
        val ouid = getOuid(ouid)
        return try {
            val encodedOuid = URLEncoder.encode(ouid, "UTF-8")
            val urlString = "$apiUrl?ouid=$encodedOuid"
            val url = URL(urlString)
            withContext(Dispatchers.IO) {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("accept", "application/json")
                connection.setRequestProperty("x-nxopen-api-key", apiKey)
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    reader.use { it.forEachLine { line -> response.append(line) } }
                    val jsonResponse = JSONObject(response.toString())
                    val racerDateCreate = jsonResponse.optString("racer_date_create", "(Unknown)")
                    if (bool) {
                        Time(racerDateCreate)
                    } else {
                        racerDateCreate
                    }
                } else {
                    "Error: Response code $responseCode"
                }
            }
        } catch (e: Exception) {
            "인터넷 연결이 되있는지 확인하세요."
        }
    }

    suspend fun timecreate(ouid: String, bool: Boolean = false): String {
        val loginDateStr = createdate(ouid)
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val loginDate = dateFormat.parse(loginDateStr) ?: return "계정 생성일 정보가 없습니다."
            val currentDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).time
            val diffInMillis = currentDate.time - loginDate.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1

            if (bool) {
                "$diffInDays"
            } else {
                "$diffInDays"
            }
        } catch (e: Exception) {
            "날짜 형식 오류: ${e.message}"
        }
    }
    suspend fun Time(date: String): String {
        return try {
            val offsetDateTime = OffsetDateTime.parse(date)
            val koreaZoneId = ZoneId.of("Asia/Seoul")
            val koreanDateTime = offsetDateTime.atZoneSameInstant(koreaZoneId).toLocalDateTime()
            val formatter =
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh시 mm분 ss초", Locale.KOREAN)
            val formattedDate = koreanDateTime.format(formatter)
            formattedDate
        } catch (e: Exception) {
            "처리도중 오류 발생"
        }
    }
}
