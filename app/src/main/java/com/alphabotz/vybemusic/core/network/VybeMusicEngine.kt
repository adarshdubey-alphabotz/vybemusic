package com.alphabotz.vybemusic.core.network

import android.util.Base64
import android.util.Log
import com.alphabotz.vybemusic.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object VybeMusicEngine {
    private const val TAG = "VybeMusicEngine"
    private const val DES_KEY = "38346591"
    private const val BASE_URL = "https://www.jiosaavn.com/api.php"
    private const val SOUNDCLOUD_CLIENT_ID = "Pb72ranhoyt6gw7hM7TkzUItXlMWSNSo"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val BAD_KEYWORDS = listOf(
        "karaoke",
        "instrumental",
        "originally perfomed",
        "tribute to",
        "baby sleep",
        "marimba",
        "piano version",
        "ringtone"
    )

    fun decryptMediaUrl(encryptedUrl: String): String {
        return try {
            val keyBytes = DES_KEY.toByteArray(Charsets.UTF_8)
            val secretKey = SecretKeySpec(keyBytes, "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(encryptedUrl, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            val decryptedUrl = String(decryptedBytes, Charsets.UTF_8)
            decryptedUrl.replace("_96.mp4", "_320.mp4")
                .replace("_96.m4a", "_320.mp4")
        } catch (e: Exception) {
            Log.e(TAG, "Decryption error for $encryptedUrl", e)
            ""
        }
    }

    val MASTER_CURATED_CATALOG: List<Track> = listOf(
        Track(
            id = "2pac_hit_em_up_outlawz",
            title = "Hit 'Em Up (Single Version)",
            artist = "2Pac",
            album = "Greatest Hits",
            durationSeconds = 312L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/7a0d1efa5902af92d7f33abc1a8a487e/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/2de08xXZilee.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vMmRlMDh4WFppbGVlLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NzAyfX19XX0_&Signature=end-pq3k2LYVoVsgf8eP23HB8LIj~YWWgWOQ4TNe6IKvwF9a2367U-TRylousU5O0nyZbivFkrak~UXMST9ejHTiqvvSVeovZ6VAEEj1396Szz-6MbRFD5z-DZW92HHFCgnGcRg5sKXv7ZjtqktSoNyDmOVjT0JhspsZBFYqCu6A8OhEPbQP~ilCuR~rpBPE6jW3VgCZO3frZ4v2xiCwUqSEddT03RyMdx~PnRhPs7mXdHAP~VJe6zG9CoXgcYAJE7DN5OxVfGZye4mDl6AZvEkH~scAYZi4WytgXZ2jB4YI4CDav80bcbyyiKzwjiqVRg5WoKEsZ819v-yJbzYv2g__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "2pac_california_love",
            title = "California Love (Original Version)",
            artist = "2Pac",
            album = "Greatest Hits",
            durationSeconds = 286L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/924830874f01bb19bfa6153f0e428e15/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/Zjr6DhLDQOxF.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vWmpyNkRoTERRT3hGLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NzM1fX19XX0_&Signature=Vg4qHSb42uxWmPZe5uyXQxq72TBsYd7STihltR4sAlF4dToQmSu2IRoSCxw2h1PIL9i3D6xMr5mEKJARpJjz~-~9-tGc02JV25oghhWREPivWqTmt2fXDfpvmZL4nomN7sA1-NOt0OpCkgjL7H4trm32HtJuAbkNzeAO8WyG3ZtTA5yW1D2O3gt67OXwLTbGnAXkpsO3qmalvhQo8XRVdoqAYbozPf8-1giAM9TaHHHxxeZUDTJBcr4NSBAtL9xnupGXN26IgbctMnfxOA-pkySokdPjtht7fKrzBTFw3t5gqt783MTHrpyF0N4cA4GOaTrBeB7cL3lPJmNBcMlZSQ__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "2pac_changes",
            title = "Changes",
            artist = "2Pac",
            album = "The Best of 2Pac",
            durationSeconds = 269L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/49856d92a31e284c7fe34b7971dba0b0/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/oE68Eiep2wL0.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vb0U2OEVpZXAyd0wwLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NzI4fX19XX0_&Signature=P2qwaxIZFIqQkiyetUIj6gC8XFKkKBSMlClcrv4bx3vhQscsj1oYJnl8Zfx8zloqkw4nAbzhjZJCLHQ~vUtcI2sFxkG7wPG64CGWvthhyv7OoCd1ecfWn5ElC-YSs5C8ANpcFJfm4ucEqzj15vx2z6suhaYCEJU3el66Wxd2DEz~OtG-ORACdhDUmfFbiqdYtW3OKQfzzcEOJpO3LN8IM-eyc9o1CT83ll1V967Z~r47MQcUnGNdpalscQxDYsq1CBmwWJQZxt-T6nrXWI9HctlxDq3isBjw8Z1ibAh1fXZve1lcmBCwv5GPqvJjaAnqxtb~DW2CEkAWSKcVgYsopw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "2pac_all_eyez_on_me",
            title = "All Eyez On Me",
            artist = "2Pac",
            album = "All Eyez On Me",
            durationSeconds = 307L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/7856515f9e28265943f125fad02f63b1/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/6CeHiNVyXTqz.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vNkNlSGlOVnlYVHF6LjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NzcyfX19XX0_&Signature=L77beARL5VMoI2D6~4UqKmMXI73gtN84jsCsDa-tm9tZtHNKIFZB-yHaQxSggEOwdxZdI9c2lYVKwy6Gw5BEoTIqi2OvnOB4Cz2p7EMHIsyXDEmU6HkassddnIwGPv-wcIKGFXkzvGVWt0q-Oey~Y~uliV928qfFafYNpYwcFltepX8-FqXqLt7AArMgvJLhtOPjRkqI3ZWE3XAfqBwdRAacd6nZl4IiORocXlcJ82AgQ7ETfLrJgF3Ar0j9NN0IMGyCmF98FyRuJiAABSqf0Xwbm5dlva~96o~nkj6NZKaUR6YSKFJd0ic9PsoFADHmpyWtmlJ1eH6w9q5CuTdbMA__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "kendrick_lamar_not_like_us",
            title = "Not Like Us",
            artist = "Kendrick Lamar",
            album = "Not Like Us",
            durationSeconds = 274L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/84345d29bc2ed8e713112425f8417e97/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/hl6ee7HMQp1K.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vaGw2ZWU3SE1RcDFLLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3Njc3fX19XX0_&Signature=KJwZLmYRcti0s78qF3JGlMBBgbGF4RG1DVz8uOWXZmur65KVzJJUL~l8zhCkerWblZX02LS0-u0DXyuLZAElyd0-J1ZmpxfQl5ms5Kyu-a0-RbJ1s4IGr5t5JDIacVLkra83ykrcaTwb50FKdPEbQyckYiJ0bZ9-u~uBhvjSRZDeqXSd8KDxzJydLDesMppW-YKkvAhhqHM9g9yl-eS0sYaDp2tVfOOKP8fiAab7ILeF5D4VTBj8M6zd3YW9YUcPXoANhqySF9CNMLixM~onoP4uewZ0dPZebGoV4I0gwfavSEpIc3zgerejhGNyfqcXiMyq5ExmRw1TcH703WaaOw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "kendrick_lamar_humble",
            title = "HUMBLE.",
            artist = "Kendrick Lamar",
            album = "DAMN.",
            durationSeconds = 177L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/7ce6b8452fae425557067db6e6a1cad5/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/qdIPCUhhgcnc.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vcWRJUENVaGhnY25jLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk4MTEzfX19XX0_&Signature=d1HHel1K5hMxlleaHPZ5ELtQDrS3Axe7vtNfMIYP9sLCpuUGSu9RGO3kNY4N3xEUCaIS7Qnu971SrxpWrsqwqg4QNVQcSA4X3TKw05KtamSy-ydFHv5IVtxdm5gZeY1byF8ibXg0Hda8LDwcH1Lv48Pf-uwGwc-TBexlZ8KW6zB7mLIW3l1finADWPgb1DSJIObYi7eCUdU6jYrhZYHijwXQeQe5ccG2TCLVZp~TD4UjN2oo8XABB4upf4EUZCGO5vKi1PcJmL2TrBvwcC9oLGW6nEx0Tw7b8my9sOYSolsnGPP6jcwWMWkFfPKuiPVTgLzUQZHA3~KU~0onCfgRvg__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "travis_scott_fe%21n_playboi_carti",
            title = "FE!N (feat. Playboi Carti)",
            artist = "Travis Scott",
            album = "UTOPIA",
            durationSeconds = 191L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/6c91e64b7157f1332a4f6b0de9e4c714/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/ZNOvZxxxrUcu.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vWk5Pdlp4eHhyVWN1LjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjE3fX19XX0_&Signature=OvFGTJsqXOIB-GBQ-BW6GdjDkFtcU4~wUp77~BNyfWsCFeqIYyxLwWoQn6THmCjatv~XXZqx8MLBFT3uX2d0VL4i5AOBeNyPhyeg0rmSsEsYSs672Gq9mgeF85CP7po-6UICBFwqDvEzMrqLtEgx8nurdXyzGSV9Wr4zKNU86mTFqOWqzeVdQnt1hWh1U2xrnfcpox7xbqFVB5wrHgmrVOW2EqxTiejh7g1WtDOgYNfcVprnxC65SlXIO0wk1KmV7ERF~EzjDQCo9ooiMqMauH51Oj35vi5pJJaBl4vXYG-leNBBowJI9r8c38-b6es6W5XFamoBfLJ3r5wr8lK6Vg__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "travis_scott_goosebumps",
            title = "Goosebumps (Remix)",
            artist = "Travis Scott",
            album = "Goosebumps (Remix)",
            durationSeconds = 162L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/6b149002c49dbb6a6056512dbfcb5e95/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/T9hBqJSOvyfK.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vVDloQnFKU092eWZLLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjU5fX19XX0_&Signature=G1QrIFNcCPr7djS0U8WCwrKP-nl0ANWZkPGUCHAJzNX4RdNRzyhuz93kbpsRGbPqBPp1kiWd4Ne-jEbmr~rZ6PRVcDR-YeyXiUT1sXvmFtS8J8FnGiR8jL5otKyqqiIFABP6VIfuFHwtQpiuenoC~S3CQucLstOM8X5teL1-tTN5T1hCh2k6rocBeM90ofzZqyL-7sZ7PVGJ1g7yVg-ZQ5vS035navQWBsJH1llz6zUt37rJVQbKq2tYCsQVR3r~Ebzq4ZkQkQ-SJhVAtDhCIaE9UwWZ98oU8EIhIeRoLEeXOm4JjtS4MfE95kWfju9yKVsDk~e0MWqo2vTxytEh5A__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "eminem_mockingbird",
            title = "Mockingbird",
            artist = "Eminem",
            album = "Curtain Call: The Hits",
            durationSeconds = 251L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/e2b36a9fda865cb2e9ed1476b6291a7d/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/Rs1Cj8T03EgL.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vUnMxQ2o4VDAzRWdMLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjkwfX19XX0_&Signature=BrrCdI4KdI5Ih1rUNdC~9aGctRdiJmSm4u2nOFeWjJ85AOqq4cZoH6VJV2yKI4TtUjvq4Q-x9WIK-ToYmj-lKzOc1Y-pc5wjRKsORNdUcf45GlJuIVGzOZ6XpG22sqDDExgO3Hr2~YfLyDXpNdS6wSA9njtCuigMnCWi97vaTEyWCPNIVohk3o6PV-Y4kop0EpqnFCHgenHvmAZPM-P87TV7sSxj7wku5ZzOvQ136WyKX7Ikq7FMpdGFXsGXKF27aeHoM7ZR1QD~D9MU~ZcPVXVfX56utoOE8l7DTy6uOrCfViPdhPn2mGN0xQ5J-E-wJLvLR8IoRxjWFKGMQMbQ0A__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "eminem_lose_yourself",
            title = "Lose Yourself",
            artist = "Eminem",
            album = "Curtain Call: The Hits",
            durationSeconds = 326L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/e2b36a9fda865cb2e9ed1476b6291a7d/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/gMH8ijqDcBmo.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vZ01IOGlqcURjQm1vLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NTgyfX19XX0_&Signature=M522rr551WoesYvvPedFXuu1SXfjEKQcZtOFtBwZLaVuh02tqW5qIqUVE9gTQYGLGkCiifeUiQA04lO9AvXzwJnl1PwTmOUnjPMdDYUfpEHZ77RIc1oTI-VQmW6qZuSJ7Gj9IthP-p645k8nz3e~E63cxaORf2I6o5AxYXRN-dE5I2PvqixWQ4KGpqewnxTw8sY2zwz~wNCGgo9AJ4eXZqgBJKUDKh-EaGkAlPlJbOHvHa6mZJqwkv777SwJCUHZWi~w5cIKaOd10o7SSYkWLPk6c10VM9YJBvG1gZPBzr7awEXhkpDn0ra1yTTM7Mcw1yQS2exJW-tSmKtsfRariw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "the_weeknd_starboy",
            title = "Starboy",
            artist = "The Weeknd",
            album = "Starboy",
            durationSeconds = 230L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/134778e4c4f19ea71c82408300925a9a/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/BogGqZmHKXCB.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vQm9nR3FabUhLWENCLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjMwfX19XX0_&Signature=QA2B9r7Q-HKQ3suuVps3855ip6OzE7e-xL2xx4zkkjT1GvoXq0MvUjs-FC92D4wIJf~LTFtkJkxWLwQEAxFLHKNMZp~SHhlp8pVWI-Wti5J3OjWxZQOl2LorcPBgtus7CbQ2TBstNYR4c5NAjvxnt2aixR9nfYUBxEz2vWneGsCycJu5~dQ8qFPTCRs4s3rIjFnXe2inVfx7keF-gMrn3TEKStiJBl82hYtBeQLmOQzSrhu6siYO~kXDLIYuwvxm5LDRLeAlsHOZh8klqRv0FAfyB4-KtvUJ~09VHxpzUCJP~~zXwL6WN4wtmN-NjrAAOTdiVSOhsyo8m~ImwUAVow__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "the_weeknd_blinding_lights",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            durationSeconds = 200L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/fd00ebd6d30d7253f813dba3bb1c66a9/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/ZPlWIUpUD7Jz.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vWlBsV0lVcFVEN0p6LjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk4MTc1fX19XX0_&Signature=ZSwTXEdBBEk573KsQWHWPybNApJCpCZ74hdeYvUxz5u82ENtanz9cVSK25lrs8Awvo6bXKR1QfQZun3UIpXDC2Nr1HJuV16el-0WcMvVt7VT5HQoTmZZBx9vBU~ph4yLTdictJjDjEmKEpX3RrAleaIGczx4zaD~u997KcVZACz7KLibkSjULrZfhciC7X4Q6Lxe-t8Q5s21jt8NjwUC94Y-QQSoTNUqr-XtY56Jk~ZHppdn6Jk87RfS0-1decnixENVM1gwZa2S1BFE8X9DcMsFNJiepwHYREXjM1NODvN7ccM3UfcJiCvAFeyBNiHIjWkLqrQVWrmDyZdKpbaWPQ__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "charlie_puth_attention",
            title = "Attention",
            artist = "Charlie Puth",
            album = "Attention",
            durationSeconds = 211L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/da7eb4c99604b2fda5f123aba3897850/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/80ubq71ZzTG8.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vODB1YnE3MVp6VEc4LjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjIwfX19XX0_&Signature=X6UZ-u84kQ5JwbydR7XSot92OCf45xygGDNVpeEiVV9FWgSCMkISJQkClYvbkrUXX5ncLHCKUXnf7iXMCSMca1pgPKjF~LRWSX04cOqhpMqXL08OQXPyGOYgCaLg1Ie8fB2wsNhwvH1kQHfriasqgVX7GorxZ3uIuh2ZjeufJkj97UCkfoJ~tm7HGip7ik1-mJvCvPxsfb2RMskCpo6TejGKQVWRa~Fk0C~2uDyJSik5U0X30wGk7y8QVlHm3bS1fyP4WToKgyfvJWutfXMAOiXpkLUaObIa5~eRGW0ufzkH1F~TbwKGf7iu6k0jRLWZIwLTGgsN306piceKuLhMIQ__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "charlie_puth_how_long",
            title = "How Long",
            artist = "Charlie Puth",
            album = "How Long",
            durationSeconds = 198L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/e8a3333f8b5382b07190a415e16248b4/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/16IIRJqoWfVG.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vMTZJSVJKcW9XZlZHLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjIxfX19XX0_&Signature=FaXC8LAP26JPOOO-~x9qw1gXiZMgQVBxf6QIS~149fLYBJ6X48jDfDtgY2y7U-18~m2t2VG5QsuyFNfaDkNr62lyJc7-RCCwKLiHJzMM3IaQFJvnbL3oRUfaCoN0US6H0cQJQpsapQNv3au8FUhBRGIF-CYOTTrnBTM~HcYLSKeROIjQpC8sy0~4WDbNA6xvBW51zx07Rydm5LOpZH8OykNM~MqnKGi9oIga1NVCUkGPJQzu5IOFIV8xXgZpag2fAa~7QoLn5spWtBGoUt~vV5N9u6V9~Gt608FAQNr3zXxUzwuoDLr8lbptWWcQJyYr0IsqFV8MAAQQrGNTF~qV6g__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "drake_gods_plan",
            title = "God's Plan",
            artist = "Drake",
            album = "Scorpion",
            durationSeconds = 198L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/b69d3bcbd130ad4cc9259de543889e30/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/nJc4lv5XyE2D.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vbkpjNGx2NVh5RTJELjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk4MDE1fX19XX0_&Signature=DfDkCCpYMEU689y9wUJFdYHI75EOPp62fvT7LsdMNnHLqz9fDFwcL3mEw0gx1ogz1-3B2inX4q5ZQLkJxysrYwiRqdAvOEfTNSXfV95RdDMy5yyPQC1brPguE8SnJHdjh3T1ouj4xJUsgRp4AW0WjGHl7h5kNq71sa3QfnAmgVN9BEFFMLKzeXTKqTD8AbgGJXziyqrGNFUuz8~Cq6GsS7rSuA1Z2RsLh16O-SxQcPI07BgxHR5FL8RzIPW9~MHbXduIomcu8ecRGu0ZhlWRDhajfBzYvdHUZF04uKNwHGoTCQe55DiiUsA4rdS2QpjN-7hQyquSsCz5DYbUN1aeGw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "wiz_khalifa_black_and_yellow",
            title = "Black and Yellow (Originally By Wiz Khalifa Karaoke / Instrumental)",
            artist = "HOT 100",
            album = "Black and Yellow - (Originally By Wiz Khalifa) [Karaoke / Instrumental] - Single",
            durationSeconds = 216L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/d72eb33cc5112d0b82798e7b3a095a7f/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/dZ3KDOONIVpZ.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vZFozS0RPT05JVnBaLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjcxfX19XX0_&Signature=YIlNILNx9fj4-p1WXOkmqu5TxKZQmc8hcqhmr1fIC8nkJAbUqk2EDNrNnHReBD4o~aYWSQqM4bB-K2qzf2eptj2W0OEii5soaNcLqyQRjl8aJhXkL9kzoGC-x8OB2SpwNrPPuvCqPJKtfTcUzt4K137U9T89kBcRAu5aX75fEm8zOeYAf6ZlttPqmxVA3w-6UiQN58~PvC6QhdndLxHDiRHvzB~crjp7DCxY~dyNavdtyeT8hIM1nYCSYWbPO-ZbuSoRDq3XOB93Teg4D6xkZW3ooFddIFpw9o~lxNJM57tpV9~fSkz2JFKbCHrkD6RKgxoqPruaGQC3YRZDDaujQw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "English"
        ),
        Track(
            id = "ap_dhillon_excuses",
            title = "Excuses",
            artist = "AP Dhillon",
            album = "Excuses",
            durationSeconds = 176L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/5391b4939914cd5b531905906aec57af/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/mjLZP34btVBd.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vbWpMWlAzNGJ0VkJkLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NTk0fX19XX0_&Signature=Kox-EJQVD2wmb3Ym9TzSzwZJ3~~HxD3MSQqD-4ATbgQLWC3G9Gqm8xJ0TXKuxr6vdkLJNGDJM6ixlTHL0DjApI0HLOdqP3Ev08zBcaU9O5cqxKPFSlbZut4ICkWoWOXeQLPvRN-f5EEbqg9hm32CfjYaLlIh2xn6qIF28-AlGhveHXImHDJ3LkPSAHO1ggQGlVFF1BSEDG7bhHHGKnEQRZ6SVEvZB~rskOr5MtMwNCVYumHn7Eo9rG00flGItKKGL5yzW6l25nXX-awK2OlseoUwaEmu7YcYvm9ycZNtYU8pFZePL6PQu19hnf18~RVuzHffeci3duHQejYTvd0eAg__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Punjabi"
        ),
        Track(
            id = "ap_dhillon_brown_munde",
            title = "AP Dhillon Brown Munde",
            artist = "Marshlemon",
            album = "AP Dhillon Brown Munde",
            durationSeconds = 129L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/d00209427d99d4dde23c11afb10fb57f/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/iQ9vjY8UcLPz.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vaVE5dmpZOFVjTFB6LjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjczfX19XX0_&Signature=HfvqBQyKjks2UWa9wEsHpgtVuX1S0HoMhVLG6DrbQ3nuzouZjuTrruLmi1cwMa3KBEPuz6K-3s9P24VQ6CTjb2krXisFneBsVNSkSDH9eq-Td71sDjbey1z2bvXI6T~Rwcnq0sJO5em~xC6YIU5l0p5NT1va1Kyhydj00Mz-l-WkrgVkPHnxHuUC~Plf2D~gyukeHHf~DuC8wm5LEoaX55DZlK~P1Y4ZMH0FWiggRH38~nJr8UoThhxgJuU-qe65C9lSspM4c03~uJp14dKpHqaJ5WLkUHpyChoYf7kmoRgtaPNgpyLmRR9fE7svwhjhFr-wtvbXnUA~xrB-H0WedQ__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Punjabi"
        ),
        Track(
            id = "sidhu_moose_wala_295",
            title = "295",
            artist = "Sidhu Moose Wala",
            album = "Moosetape",
            durationSeconds = 270L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/fb6aff16c7949105369a088f1914ab2a/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/xAUZQyJNkthw.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20veEFVWlF5Sk5rdGh3LjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5MzI2MTAzfX19XX0_&Signature=Jk9KqMsVel6KUC7totZ2WGgL9n0RjNjRbwV-sM~OI3oBAPbc1OE8RRCzAgBFKxXUYj5U5JF8afz0umC-ZbAgSNrurwz6hHy6RMOTfowqwK7A0Wise63J7iDD~E6gaM6MjnPym4925vzUtlg82yUuFGGnkS3fKK0PeCePX~HTKFbgey9lfsSH1zgO7R15MdbYF7JhnGjqyW4GprGt3lmoyJaZpqxEzBSvOi-OWJUkavOgRqvm6VKgSwWZj1ULxZQGMY2DTCRGfg9AnsvXCeqyYxzTTVG5KUlIs~bU~8CiC~i~yIuV8jGfSBp-vPnus2r1RmiR9S0UqUiwAII0mJKR0w__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Punjabi"
        ),
        Track(
            id = "diljit_dosanjh_lover",
            title = "Lover",
            artist = "Diljit Dosanjh",
            album = "MoonChild Era",
            durationSeconds = 190L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/a8cf2b35efa2c9a9bc1c9b0bcbee93ca/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/2loxpdJvm733.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vMmxveHBkSnZtNzMzLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjA4fX19XX0_&Signature=YfxiZ1i6CQLiQA1GIVNTacHhCRaaer-auiCGuHaRta4GW8Jjvfm7-GHoK9sSY5aymavkngd2hVg6JTvIeRfAPtObAQzliLvVrU-XJhh90Ub7tpboaGpLJygRU~Uia6do28v4x7QISTTT3xd5XbhtTdaakQ7sxMJTaLoDl8N81obh~kZb1MH6t4HoFpNmaGA04wVOr0I8c7ELJG~pgvy7Jh2AaKP8goGVCcU~4kTZDqVmm8kccJtIy15j9~hJzBs2Ymxaso05cDrxdOo17KKZGU9E9eXqQCtZY0oUv3SOHa~5O8mIdE4Wl9vIbe-0Oq43Xp-U2L2QHi~PDZmOLRtrCw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Punjabi"
        ),
        Track(
            id = "karan_aujla_softly",
            title = "Softly",
            artist = "Karan Aujla",
            album = "Making Memories",
            durationSeconds = 155L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/22f4e4606f6b9a7bad96f74fa42c3778/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/9CyA9BAc5IKW.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vOUN5QTlCQWM1SUtXLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjE4fX19XX0_&Signature=UqSUBkxdXFzq2NeDCt-abbB5o7GFIYmBp-cWvJAZvsxjK-X2JyDVVvteDWsa~YGmCEA0sgkXISPFXitfZ6pdZ7HLMEZ5Mh9vTyp0iqvYcElcSAgFot2e5-jmPMQJzObg2vWORG9jgnbfSJGSX2t80kNs0fVEx7uxJpc3gdN4dCWtbcLSyGiCuLNoRRGpVyn8dkto33c8pYPREaQgyUIvabIXMFclkXkhfR6ug0BsSpLMAVt9Waa2BdwnViLhlWKQvrKa4K-PDozQ3g32zbl521ru234errsRAe1Eooi8oeFyNhmQ0KnW7FT9F-vmC-To-qaN--I0XPUWU8TC1C1jKg__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Punjabi"
        ),
        Track(
            id = "shubh_cheques",
            title = "Cheques",
            artist = "Shubh",
            album = "Still Rollin",
            durationSeconds = 183L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/447c91c56795daef15d61f8977a82e9c/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/2dXp7feTA4dO.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vMmRYcDdmZVRBNGRPLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjA2fX19XX0_&Signature=GwUuPAaFtV9nzOr2dKCPANzr-mHSoGrO-BERobXfJr~LKcBx69Kwazd59fNkB07qPbcUhW6CesH3lA3dRpXED2pU-ltmfRTTckcXZItrt7vqiRgkVghjW4fOv3Teo3zMJBJweUF-~CjvN6KF0dwXSgAflUB8zPVzpd21WsvozZoAgywZpdRN2fyNA02RZQhMdIhVjGaezVFGlE848qAirsSG5nCK5d5bpGVlty7iXfWC7qhWT94nHwn42MCV20BvazYDqbEsw2x~T4xIONEBgYY70vwKSG9u3V7dS2BBWG82075W~Pgj1fQxKfBZ1GJnwNR7t4KZje9x9wAllTzoGQ__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Punjabi"
        ),
        Track(
            id = "arijit_singh_kesariya",
            title = "Kesariya",
            artist = "Pritam",
            album = "Kesariya",
            durationSeconds = 257L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/d7dd52dd5b1341d48aca9930f04db8e9/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/ebcivEWjSHLL.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vZWJjaXZFV2pTSExMLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjE0fX19XX0_&Signature=AfuNNE19qrjQ0zgmFzAvQPXZQlD5w-EG9lsol65NlPImRXzFwq6V6FSZ6ASglVehEbQ7gTpdi2fYGxtNqrWSNmLsDJ~il9tNU-bbO7LwqoQeIm7F2gjeQvfUSygT7NqBWhGkWvrZQZrr71uNnQaWo0NVI7jShASFG8GbnZYFUbP5m4n~0YzU7jkswVKcPLuhjzoUA-LqbMWH0FiCHnM~8Qrivq69daFqTc7j4jtFnI9xLzD4IRllpELxx8MOqOdh8CVGUBDAZIusGwGEuyuxbGCe21Fsda2nYuBRo4u4ZYK7zSy09A0ewWgDYuekwNSifryvUYhgQc4QVjT4mnE0Qw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Hindi"
        ),
        Track(
            id = "arijit_singh_apna_bana_le",
            title = "Apna Bana Le",
            artist = "Sachin-Jigar",
            album = "Baarish Bollywood Mix",
            durationSeconds = 261L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/5e2aaa0f0a9b4bccfdf01c447f2e169c/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/VgFqEWYsOwRb.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vVmdGcUVXWXNPd1JiLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjU1fX19XX0_&Signature=EZssbGqcD1qa~RURBxoQ8LH4dvA4LAflvMgJbkyrWmn3CA0t~F-pd5CJKTLstARgCusp6b3QZfcNpxMN8ZNHa2MBVttKUMTfcCIJAF6N1o5RWtFiZksy5U3HpyXBt8vHbwyKjCA3KlQKLVBP7rGCvnkyuH9J0IwP4caodAbuvbpzfA6v0aVj3bXJVl8TyRfEUgoBkTOlw6GTYORSAW68Za-qIcbuTNsIG9LcEgzaXOWA6Cy23XNbSZzVAz3h4-tk9vIFjrmjPVNZPiLBJSjfhn3RF3gIJOT1wB~Wld0Gho9hUU0ieViQtR84vLk10Ysrfexf3tzJIe5DFRnBXiVbyg__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Hindi"
        ),
        Track(
            id = "arijit_singh_chaleya",
            title = "Chaleya (From \"Jawan\")",
            artist = "Arijit Singh",
            album = "Chaleya (From \"Jawan\")",
            durationSeconds = 200L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/87965798331705639c8965c7fc100ffc/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/TUp8CyM1VKXP.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vVFVwOEN5TTFWS1hQLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3NjEzfX19XX0_&Signature=Rqf7ZUEa~PfGnTB0JTvecbFavkR3ip35bZaIYn-AC7Z0On676Hcbzri9Yh6I~F78UzkXdhZNQmfKFlg7TbP-J1HFfm08HWDMREfj24RmmcWkQQazqY8ifbL~~aX4f2kJXu9Qxm48md74fNI1WUQPXg8X5p-95SrolViVMWP5OhEUmhUM8rv1mX6MVaeR2Rpam9SJbftCpgDASBktEM0ecTUzudSjYdplhTQdqfXjKBvgiVU9s9wcfq3YIuQQX0Ka578U4UsofPyh58Kl59jKz6NzZR7j38nD6mIM4DMGsK5ZbE95E1HXvPiyLA7aXnxAer8s6-0ZeGxpjcdBxWflfw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Hindi"
        ),
        Track(
            id = "vishal_mishra_pehle_bhi_main",
            title = "Satranga X Pehle Bhi Main Mashup",
            artist = "Arijit Singh",
            album = "Satranga X Pehle Bhi Main Mashup",
            durationSeconds = 236L,
            artworkUrl = "https://cdn-images.dzcdn.net/images/cover/e8503eb01fce97c7427b794e8cd3c478/1000x1000-000000-80-0-0.jpg",
            streamUrl = "https://cf-media.sndcdn.com/cHd7uZIbblE2.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vY0hkN3VaSWJibEUyLjEyOC5tcDMqIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNzg5Mjk3Njc1fX19XX0_&Signature=CwPLtWkDO8~4IAutyJZ5HZ0CAngkXHKjRhu-MvW-nmSiuSzFZd12DrsFibHbcCrh-IWD-ikgE93ZqwlE2Jdygd3A6JfU1BKGQsEndMa1i8qr4UlD~5T8OEfZ3HfoB7Zdcq~X1iliUVfaTwu03UIGFhA4CuzEDiQ-oM9eNTNgv~rFL3eRNNZGcY0vBDIf1llFPh4qu5yPHrTowkcM9Ql5QMR56lkPRkjJcE16LZlrw1lW8nonr6Rhs6Ttt9Pzz85oPiRw97Lhcpleouz7zwe9amEWzesQEkdpwVCb6ixoA9E3u~MNfsjtQPgFkCKkyGfzdRwlDkqPTrRg-xhg0sbnYw__&Key-Pair-Id=APKAI6TU7MMXM5DG6EPQ",
            audioQuality = "320kbps Studio Master",
            language = "Hindi"
        )
    )

    fun getInitialSeedTracks(): List<Track> {
        return MASTER_CURATED_CATALOG
    }

    suspend fun resolveStreamUrl(track: Track): String = withContext(Dispatchers.IO) {
        if (track.streamUrl.startsWith("https://aac.saavncdn.com") && track.streamUrl.endsWith(".mp4")) {
            return@withContext track.streamUrl
        }

        if (track.streamUrl.isNotBlank() && !track.streamUrl.startsWith("resolve:")) {
            try {
                val headReq = Request.Builder().url(track.streamUrl).head().build()
                val resp = client.newCall(headReq).execute()
                if (resp.isSuccessful) {
                    return@withContext track.streamUrl
                }
            } catch (e: Exception) {
                // Fallthrough to dynamic resolve
            }
        }

        val freshStream = resolveSoundCloudStream("${track.title} ${track.artist}")
        if (!freshStream.isNullOrBlank()) {
            return@withContext freshStream
        }

        track.streamUrl
    }

    suspend fun resolveSoundCloudStream(query: String): String? = withContext(Dispatchers.IO) {
        try {
            val eq = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "https://api-v2.soundcloud.com/search/tracks?q=$eq&client_id=$SOUNDCLOUD_CLIENT_ID&limit=5"
            val req = Request.Builder().url(searchUrl).header("User-Agent", "Mozilla/5.0").build()
            val body = client.newCall(req).execute().body?.string() ?: return@withContext null
            val json = JSONObject(body)
            val collection = json.optJSONArray("collection") ?: return@withContext null

            for (i in 0 until collection.length()) {
                val tObj = collection.optJSONObject(i) ?: continue
                val media = tObj.optJSONObject("media") ?: continue
                val transcodings = media.optJSONArray("transcodings") ?: continue
                for (j in 0 until transcodings.length()) {
                    val mObj = transcodings.optJSONObject(j) ?: continue
                    val format = mObj.optJSONObject("format") ?: continue
                    val protocol = format.optString("protocol")
                    val streamEndpoint = mObj.optString("url")
                    if (protocol == "progressive" && !streamEndpoint.contains("preview") && streamEndpoint.isNotBlank()) {
                        val epReq = Request.Builder().url("$streamEndpoint?client_id=$SOUNDCLOUD_CLIENT_ID").header("User-Agent", "Mozilla/5.0").build()
                        val epBody = client.newCall(epReq).execute().body?.string() ?: continue
                        val epJson = JSONObject(epBody)
                        val directUrl = epJson.optString("url")
                        if (directUrl.isNotBlank()) return@withContext directUrl
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "resolveSoundCloudStream error for $query", e)
            null
        }
    }

    suspend fun searchDeezerTracks(query: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val eq = URLEncoder.encode(query, "UTF-8")
            val url = "https://api.deezer.com/search?q=$eq&limit=15"
            val req = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build()
            val body = client.newCall(req).execute().body?.string() ?: return@withContext emptyList()
            val json = JSONObject(body)
            val data = json.optJSONArray("data") ?: return@withContext emptyList()

            val list = mutableListOf<Track>()
            for (i in 0 until data.length()) {
                val item = data.optJSONObject(i) ?: continue
                val id = "dz_" + item.optLong("id")
                val title = item.optString("title", "Unknown")
                val artistObj = item.optJSONObject("artist")
                val artistName = artistObj?.optString("name", "Unknown Artist") ?: "Unknown Artist"
                val albumObj = item.optJSONObject("album")
                val albumTitle = albumObj?.optString("title", "Single") ?: "Single"
                val cover = albumObj?.optString("cover_xl")
                    ?: albumObj?.optString("cover_big")
                    ?: albumObj?.optString("cover_medium")
                    ?: ""
                val preview = item.optString("preview", "")
                val duration = item.optLong("duration", 0L)

                list.add(
                    Track(
                        id = id,
                        title = title,
                        artist = artistName,
                        album = albumTitle,
                        durationSeconds = duration,
                        artworkUrl = cover,
                        streamUrl = preview,
                        audioQuality = "320kbps Studio Master",
                        language = "English"
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "searchDeezerTracks error for $query", e)
            emptyList()
        }
    }

    suspend fun getCuratedFeedForArtists(favoriteArtists: Set<String>): List<Track> = withContext(Dispatchers.IO) {
        if (favoriteArtists.isEmpty()) {
            return@withContext MASTER_CURATED_CATALOG
        }

        val matchedTracks = MASTER_CURATED_CATALOG.filter { track ->
            favoriteArtists.any { artist ->
                track.artist.contains(artist, ignoreCase = true) ||
                        track.title.contains(artist, ignoreCase = true)
            }
        }.toMutableList()

        val queryArtists = favoriteArtists.take(3)
        val apiDeferred = queryArtists.map { artistName ->
            async {
                searchTracks(artistName, limit = 8)
            }
        }
        val apiResults = apiDeferred.awaitAll().flatten()

        for (t in apiResults) {
            if (matchedTracks.none { it.id == t.id || it.title.equals(t.title, ignoreCase = true) }) {
                matchedTracks.add(t)
            }
        }

        if (matchedTracks.isEmpty()) {
            MASTER_CURATED_CATALOG
        } else {
            matchedTracks.shuffled()
        }
    }

    suspend fun getSimilarTracks(track: Track): List<Track> = withContext(Dispatchers.IO) {
        val titleLower = track.title.lowercase().trim()
        val artistLower = track.artist.lowercase().trim()
        val langLower = track.language.lowercase().trim()

        fun isEligible(candidate: Track): Boolean {
            if (candidate.id == track.id) return false
            val candTitle = candidate.title.lowercase().trim()
            if (candTitle == titleLower || candTitle.contains(titleLower) || titleLower.contains(candTitle)) return false
            return true
        }

        // 1. If Hip-Hop / Western Rap (2Pac, Eminem, Travis, Kendrick, Drake, Wiz, etc.)
        if (artistLower.contains("2pac") || artistLower.contains("tupac") ||
            artistLower.contains("eminem") || artistLower.contains("travis") ||
            artistLower.contains("kendrick") || artistLower.contains("drake") ||
            artistLower.contains("weeknd") || artistLower.contains("charlie") ||
            artistLower.contains("wiz") || langLower.contains("english")
        ) {
            val hipHopTracks = MASTER_CURATED_CATALOG.filter {
                isEligible(it) && it.language.equals("English", ignoreCase = true)
            }
            if (hipHopTracks.isNotEmpty()) return@withContext hipHopTracks.shuffled()
        }

        // 2. If Punjabi (AP Dhillon, Sidhu, Diljit, Aujla, Shubh)
        if (artistLower.contains("ap dhillon") || artistLower.contains("sidhu") ||
            artistLower.contains("diljit") || artistLower.contains("karan aujla") ||
            artistLower.contains("shubh") || langLower.contains("punjabi")
        ) {
            val punjabiTracks = MASTER_CURATED_CATALOG.filter {
                isEligible(it) && it.language.equals("Punjabi", ignoreCase = true)
            }
            if (punjabiTracks.isNotEmpty()) return@withContext punjabiTracks.shuffled()
        }

        // 3. If Bollywood / Hindi (Arijit, Atif, Vishal Mishra, Pritam)
        val hindiTracks = MASTER_CURATED_CATALOG.filter {
            isEligible(it) && it.language.equals("Hindi", ignoreCase = true)
        }
        if (hindiTracks.isNotEmpty()) return@withContext hindiTracks.shuffled()

        // Fallback
        MASTER_CURATED_CATALOG.filter { isEligible(it) }.shuffled()
    }

    suspend fun searchTracks(query: String, page: Int = 1, limit: Int = 25): List<Track> = withContext(Dispatchers.IO) {
        try {
            val localMatches = MASTER_CURATED_CATALOG.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true) ||
                        it.album.contains(query, ignoreCase = true)
            }

            val deezerMatches = async { searchDeezerTracks(query) }
            val saavnMatches = async {
                try {
                    val encodedQuery = URLEncoder.encode(query, "UTF-8")
                    val url = "$BASE_URL?__call=search.getResults&_format=json&_marker=0&api_version=4&ctx=web6dot0&q=$encodedQuery&p=$page&n=$limit"
                    val request = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile)").build()
                    val body = client.newCall(request).execute().body?.string() ?: return@async emptyList()
                    val json = JSONObject(body)
                    val results = json.optJSONArray("results") ?: return@async emptyList()
                    parseTrackList(results)
                } catch (e: Exception) {
                    emptyList()
                }
            }

            val dResults = deezerMatches.await()
            val sResults = saavnMatches.await()

            val combined = (localMatches + dResults + sResults)
                .distinctBy { (it.title.trim().lowercase()) to (it.artist.trim().lowercase()) }

            if (combined.isNotEmpty()) combined else localMatches
        } catch (e: Exception) {
            Log.e(TAG, "searchTracks error for $query", e)
            MASTER_CURATED_CATALOG.filter {
                it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun getTrendingTracks(genreOrMood: String = "trending"): List<Track> = withContext(Dispatchers.IO) {
        val query = when (genreOrMood.lowercase()) {
            "⚡ new release", "new release" -> "latest top hits"
            "🔥 trending", "trending" -> "top trending songs"
            "🎧 lo-fi", "lofi" -> "lofi chill study"
            "🌙 night drive" -> "night drive phonk beats"
            "🧠 focus", "focus" -> "deep focus ambient"
            else -> "top hits songs"
        }

        val networkTracks = searchTracks(query, page = 1, limit = 25)
        if (networkTracks.isNotEmpty()) {
            networkTracks
        } else {
            getInitialSeedTracks()
        }
    }

    private fun parseTrackList(array: JSONArray): List<Track> {
        val tracks = mutableListOf<Track>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val track = parseTrackJson(obj)
            if (track != null) {
                tracks.add(track)
            }
        }
        return tracks
    }

    private fun parseTrackJson(obj: JSONObject): Track? {
        return try {
            val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return null
            val rawTitle = obj.optString("title", "Unknown Track")
            val title = cleanHtml(rawTitle)

            val moreInfo = obj.optJSONObject("more_info") ?: JSONObject()

            val subtitle = obj.optString("subtitle")
            val singers = moreInfo.optString("singers")
            val artistMap = moreInfo.optJSONObject("artistMap")
            val primaryArtistsArray = artistMap?.optJSONArray("primary_artists")
            val primaryArtistNames = if (primaryArtistsArray != null && primaryArtistsArray.length() > 0) {
                val names = mutableListOf<String>()
                for (j in 0 until primaryArtistsArray.length()) {
                    val aObj = primaryArtistsArray.optJSONObject(j)
                    val aName = aObj?.optString("name")
                    if (!aName.isNullOrBlank()) names.add(aName)
                }
                names.joinToString(", ")
            } else null

            val artist = cleanHtml(
                primaryArtistNames
                    ?: singers.takeIf { it.isNotBlank() }
                    ?: subtitle.takeIf { it.isNotBlank() }
                    ?: "Various Artists"
            )

            val lowerTitle = title.lowercase()
            val lowerArtist = artist.lowercase()
            if (BAD_KEYWORDS.any { lowerTitle.contains(it) || lowerArtist.contains(it) }) {
                return null
            }

            val album = cleanHtml(moreInfo.optString("album", "Vybe Single"))
            val durationSecs = moreInfo.optString("duration", "0").toLongOrNull() ?: 0L

            val rawImage = obj.optString("image", "")
            val artworkUrl = rawImage.replace("150x150", "500x500")
                .replace("50x50", "500x500")
                .replace("http://", "https://")

            val encMediaUrl = moreInfo.optString("encrypted_media_url", "")
            val streamUrl = if (encMediaUrl.isNotBlank()) {
                decryptMediaUrl(encMediaUrl)
            } else {
                moreInfo.optString("vlink", "")
            }

            if (streamUrl.isBlank()) return null

            Track(
                id = id,
                title = title,
                artist = artist,
                album = album,
                durationSeconds = durationSecs,
                artworkUrl = artworkUrl,
                streamUrl = streamUrl,
                audioQuality = "320kbps Lossless AAC",
                language = obj.optString("language", "English")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun cleanHtml(str: String): String {
        return str.replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&#039;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&apos;", "'")
            .trim()
    }
}
