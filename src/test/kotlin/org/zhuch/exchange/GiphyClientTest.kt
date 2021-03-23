package org.zhuch.exchange

import org.zhuch.exchange.gif.IGiphyClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerSettings
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.kotlin.test.test
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@MockServerSettings(ports = [9090])
class GiphyClientTest @Autowired constructor(
	private val giphy: IGiphyClient
) {

	private lateinit var client: ClientAndServer

	@BeforeEach
	fun initServer(client: ClientAndServer) {
		this.client = client
	}

	@AfterEach
	fun cleanUpData() {
		client.reset()
	}

	@Test
	fun `test rich query`() {
		val query = "rich"
		val random = Random.nextInt(0, 100)

		successfulResponse(client, query, random)

		giphy.searchGif(query, 1, random)
			.test()
			.expectNextMatches {
				it.data?.get(0)!!.images!!.downsized!!.url!! == GIF_URL
			}
			.verifyComplete()
	}

	@Test
	fun `test broke query`() {
		val query = "broke"
		val random = Random.nextInt(0, 100)

		successfulResponse(client, query, random)

		giphy.searchGif(query, 1, random)
			.test()
			.expectNextMatches {
				it.data?.get(0)!!.images!!.downsized!!.url!! == GIF_URL
			}
			.verifyComplete()
	}

	companion object {
		fun successfulResponse(client: ClientAndServer, query: String, random: Int) {
			client.`when`(getGifRequest(query, random), Times.exactly(1))
				.respond(
					HttpResponse.response()
						.withStatusCode(200)
						.withBody(JsonBody(getResourceAsString("giphy_response.json")))
				)
		}

		private fun getGifRequest(query: String, random: Int): HttpRequest {
			return HttpRequest.request()
				.withMethod("GET")
				.withPath("/search")
				.withQueryStringParameter("limit","1")
				.withQueryStringParameter("q", query)
				.withQueryStringParameter("offset","$random")
				.withQueryStringParameter("api_key","test")
		}

		private fun getResourceAsString(url: String): String {
			return this::class.java.classLoader.getResource(url)!!.readText()
		}

		const val GIF_URL = "https://media0.giphy.com/media/BZPv2nPrHYiaM0LJNE/giphy-downsized.gif?cid=d75496129kpy6b72h5qv2y3i4nxeydu9kea8nikmmm354lcf&rid=giphy-downsized.gif"
	}
}
