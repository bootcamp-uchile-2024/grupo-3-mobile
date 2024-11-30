import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlantApi {
    @GET("catalogo")
    fun getPlants(@Query("page") page: Int, @Query("pageSize") size: Int): Call<List<Plant>>
}
