package dduw.com.mobile.finalproject.data.network.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.Throws

// 새로운 버전의 NetworkUtil 클래스
class NetworkUtil(val context: Context) {

    val TAG = "NetworkUtil"

    companion object {
        val GET = "GET"
        val POST = "POST"
    }

    /**
     * GET 요청을 위한 URL에 쿼리 파라미터를 추가합
     *
     * @param address String 기본 URL 주소
     * @param data Map<String, String>? 추가할 쿼리 파라미터. key는 파라미터 이름, value는 파라미터 값
     * @return String 쿼리 파라미터가 추가된 완성된 URL
     */
    private fun addGetParams(address: String, data: Map<String, String>?) : String {
        var addressBuilder = StringBuilder(address)

        // GET 방식일 경우 addres에 parameters 추가
        addressBuilder = addressBuilder.append("?")
        data?.forEach { (key, value) ->
            addressBuilder.append("$key=$value&")
        }
        addressBuilder.deleteCharAt(addressBuilder.lastIndex)

        Log.d(TAG, addressBuilder.toString())

        return addressBuilder.toString()
    }


    /**
     * GET 또는 POST 요청을 처리
     *
     * @param requestMethod String GET 또는 POST
     * @param address String 서버 주소
     * @param data String? 서버에 GET 또는 POST 방식으로 전달할 매개변수
     * @return InputStream? Server 요청결과
     */
    @Throws(IOException::class, SocketTimeoutException::class)
    fun sendRequest(request: String, address: String, data: Map<String, String>?) : InputStream? {

        var url : URL =  if (request == "GET") {
            URL(addGetParams(address, data))        // GET 방식일 경우 address 에 parameters 추가
        } else {
            URL(address)
        }

        Log.d(TAG, url.toString())
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 5000
            connectTimeout = 5000
            requestMethod = "GET"
            doInput = true
            //연결
            connect()
            //만들어진 inputStream 반환
            inputStream
        }

    }


    fun getNetworkInfo() : String {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false

        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network)?.apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }

        val result = StringBuilder()
        result.append("Wifi connected: $isWifiConn\n")
        result.append("Mobile connected: $isMobileConn\n")

        Log.d(TAG, "Wifi connected: $isWifiConn")
        Log.d(TAG, "Mobile connected: $isMobileConn")

        return result.toString()
    }


    fun isOnline(): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }


}