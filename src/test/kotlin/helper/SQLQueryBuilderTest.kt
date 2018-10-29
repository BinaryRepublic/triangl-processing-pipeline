package helper

import com.triangl.processing.helper.SQLQueryBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import testSupport.MockData

@RunWith(MockitoJUnitRunner::class)
class SQLQueryBuilderTest {

    private val mockData = MockData()

    @Test
    fun `should build select query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")

        // when
        val query = sqlQueryBuilder.select("c1")

        // then
        assertThat(query).isEqualTo("SELECT * FROM Customer WHERE id=\"c1\"")
    }

    @Test
    fun `should build insert query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")
        val customer = mockData.customerOutput("c1")

        // when
        val query = sqlQueryBuilder.insert(customer.toHashMap())

        // then
        assertThat(query).isEqualTo("INSERT INTO Customer (name, createdAt, id, lastUpdatedAt) VALUES (\"name_c1\", \"2018.01.01 00:00:00\", \"c1\", \"2018.01.01 00:00:00\")")
    }

    @Test
    fun `should build update query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")
        val customer = mockData.customerOutput("c1")

        // when
        val query = sqlQueryBuilder.update(customer.toHashMap())

        // then
        assertThat(query).isEqualTo("UPDATE Customer SET name=\"name_c1\", createdAt=\"2018.01.01 00:00:00\", lastUpdatedAt=\"2018.01.01 00:00:00\" WHERE id=\"c1\"")
    }

    @Test
    fun `should build delete query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")

        // when
        val query = sqlQueryBuilder.delete("c1")

        // then
        assertThat(query).isEqualTo("DELETE FROM Customer WHERE id=\"c1\"")
    }

    @Test
    fun `should build deleteNotIn query for map`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Map")
        val data = listOf(
            mockData.mapOutput("m1", "c1"),
            mockData.mapOutput("m2", "c1")
        )

        // when
        val query = sqlQueryBuilder.deleteNotIn(data)

        // then
        assertThat(query).isEqualTo("DELETE FROM Map WHERE id NOT IN (\"m1\", \"m2\") AND customerId=\"c1\"")
    }

    @Test
    fun `should build deleteNotIn query for router`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Router")
        val data = listOf(
            mockData.routerOutput("r1", "c1", "m1"),
            mockData.routerOutput("r2", "c2", "m1")
        )

        // when
        val query = sqlQueryBuilder.deleteNotIn(data)

        // then
        assertThat(query).isEqualTo("DELETE FROM Coordinate WHERE id IN (SELECT coordinateId FROM Router WHERE id NOT IN (\"r1\", \"r2\") AND mapId=\"m1\")); DELETE FROM Router WHERE id NOT IN (\"r1\", \"r2\") AND mapId=\"m1\";")
    }

    @Test
    fun `should format strings for queries`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")
        val string = "string"

        // when
        val result = sqlQueryBuilder.formatValueForQuery(string)

        // then
        assertThat(result).isEqualTo("\"string\"")
    }

    @Test
    fun `should format dates for queries`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")
        val date = mockData.defaultDate

        // when
        val result = sqlQueryBuilder.formatValueForQuery(date)

        // then
        assertThat(result).isEqualTo("\"2018.01.01 00:00:00\"")
    }

    @Test
    fun `should construct IN clause`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder("Customer")
        val values = listOf("v1", "v2", "v3")

        // when
        val inClause = sqlQueryBuilder.constructINClause(values)

        // then
        assertThat(inClause).isEqualTo("\"v1\", \"v2\", \"v3\"")
    }
}