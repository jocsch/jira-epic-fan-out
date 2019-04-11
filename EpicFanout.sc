import $file.Vars

@main
def main(epic: String): Unit = {
	val domain = Vars.domain
	val r = requests.get(s"$domain/rest/api/3/issue/$epic", auth = new requests.RequestAuth.Basic(Vars.user, Vars.token)).text 
	val json = ujson.read(r)

	val epicInput = Map[String, ujson.Value]( 
	  "summary" -> json("fields")("summary"),
	  "description" -> json("fields")("description"),
	  "project" -> json("fields")("project")("id"),
	  "reporter" -> json("fields")("reporter")("accountId"),
	  "priority" -> json("fields")("priority")("id"),
	  "fixVersions" -> json("fields")("fixVersions").arr.map(_.obj.filterKeys(_ == "id")), 
	  "components" -> json("fields")("components").arr.map(c => (c("id").str, c("name").str))
	) 

	val idgen = (id: ujson.Value) => ujson.Obj("id" -> id) 

	epicInput("components")
		.obj
		.filter(t => Vars.componentFilter.isEmpty || Vars.componentFilter.contains(t._2.str))
		.foreach{ case (id, title) => 

		val storyFields = ujson.Obj("fields" -> ujson.Obj("summary" -> ujson.Str(title.str + " - " + epicInput("summary").str), 
								  "issuetype" -> ujson.Obj("id" -> ujson.Str("10001")),
								  "components" -> ujson.Arr(ujson.Obj("id" -> ujson.Str(id))),
								  "project" -> idgen(epicInput("project")),
								  "description" -> epicInput("description"),
								  "fixVersions" -> epicInput("fixVersions"),
								  "reporter" ->  idgen(epicInput("reporter")),
								  "customfield_10016" -> ujson.Str(epic),
								  "priority" -> idgen(epicInput("priority"))))

		val resp = requests.post(s"$domain/rest/api/3/issue", auth = new requests.RequestAuth.Basic(Vars.user, Vars.token),
										headers = Map("Content-Type" -> "application/json"), 	
										data = ujson.write(storyFields))

		if (resp.statusCode != 201) {
			println(s"Failed at component $title with messages: " + resp.text)
		} else {
			println(s"$title " + ujson.read(resp.text)("key").str)
		}
	}
}
