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
    fun `should build insert query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val customer = mockData.customerOutput("c1")

        // when
        val query = sqlQueryBuilder.insert(customer.toHashMap(), "Customer")

        // then
        assertThat(query).isEqualTo("INSERT INTO Customer (name, createdAt, id, lastUpdatedAt) VALUES (\"name_c1\", \"2018.01.01 00:00:00\", \"c1\", \"2018.01.01 00:00:00\")")
    }

    @Test
    fun `should build insert or update query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val customer = mockData.customerOutput("c1")

        // when
        val query = sqlQueryBuilder.insertOrUpdate(customer.toHashMap(), "Customer")

        // then
        assertThat(query).isEqualTo("INSERT INTO Customer (name, createdAt, id, lastUpdatedAt) VALUES (\"name_c1\", \"2018.01.01 00:00:00\", \"c1\", \"2018.01.01 00:00:00\") ON DUPLICATE KEY UPDATE name=VALUES(name), createdAt=VALUES(createdAt), lastUpdatedAt=VALUES(lastUpdatedAt)")
    }

    @Test
    fun `should build delete query`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()

        // when
        val query = sqlQueryBuilder.delete("c1", "Customer")

        // then
        assertThat(query).isEqualTo("DELETE FROM Customer WHERE id=\"c1\"")
    }

    @Test
    fun `should build deleteNotIn query for map`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val data = listOf(
            mockData.mapOutput("m1", "c1"),
            mockData.mapOutput("m2", "c1")
        )

        // when
        val queries = sqlQueryBuilder.deleteNotIn(data, "Map")

        // then
        assertThat(queries.size).isEqualTo(1)
        assertThat(queries[0]).isEqualTo("DELETE FROM Map WHERE id NOT IN (\"m1\", \"m2\") AND customerId=\"c1\"")
    }

    @Test
    fun `should build deleteNotIn query for router`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val data = listOf(
            mockData.routerOutput("r1", "c1", "m1"),
            mockData.routerOutput("r2", "c2", "m1")
        )

        // when
        val queries = sqlQueryBuilder.deleteNotIn(data, "Router")

        // then
        assertThat(queries.size).isEqualTo(2)
        assertThat(queries[0]).isEqualTo("DELETE FROM Coordinate WHERE id IN (SELECT coordinateId FROM Router WHERE id NOT IN (\"r1\", \"r2\") AND mapId=\"m1\")")
        assertThat(queries[1]).isEqualTo("DELETE FROM Router WHERE id NOT IN (\"r1\", \"r2\") AND mapId=\"m1\"")
    }

    @Test
    fun `should format strings for queries`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val string = "string"

        // when
        val result = sqlQueryBuilder.formatValueForQuery(string)

        // then
        assertThat(result).isEqualTo("\"string\"")
    }

    @Test
    fun `should format dates for queries`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val date = mockData.defaultDate

        // when
        val result = sqlQueryBuilder.formatValueForQuery(date)

        // then
        assertThat(result).isEqualTo("\"2018.01.01 00:00:00\"")
    }

    @Test
    fun `should construct IN clause`() {
        // given
        val sqlQueryBuilder = SQLQueryBuilder()
        val values = listOf("v1", "v2", "v3")

        // when
        val inClause = sqlQueryBuilder.constructINClause(values)

        // then
        assertThat(inClause).isEqualTo("\"v1\", \"v2\", \"v3\"")
    }
}