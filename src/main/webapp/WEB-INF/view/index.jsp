<html>
<head>

	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>

	<title>Infobip Task Scheduler</title>

	<link rel="shortcut icon" href="/static/img/favicon.ico"/>

	<link rel="stylesheet" href="/static/ext-4.0.7-gpl/resources/css/ext-all-gray.css"/>
	<link rel="stylesheet" href="/static/bootstrap/css/bootstrap.min.css"/>
	<link rel="stylesheet" href="/static/bootstrap/css/bootstrap-responsive.min.css"/>

	<link rel="stylesheet" href="/static/infobip/css/bootstrap-fix.min.css"/>
	<link rel="stylesheet" href="/static/infobip/css/ext-fix.min.css"/>

</head>
<body>

	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</a>
				<a class="brand" href="/">Infobip Task Scheduler</a>
				<div class="nav-collapse">
					<ul class="nav">
						<li><a href="#task-schedule">Task Schedule</a></li>
						<li><a href="#api">API</a></li>
						<li><a href="#changelog">Changelog</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>

	<div class="container">

		<section id="task-schedule"></section>
		<div class="page-header">
			<h2>Task Schedule <small>Manage HTTP tasks &amp; trace logs</small></h2>
		</div>

		<div id="grid-div"></div>

		<section id="api"></section>

		<div class="page-header">
			<h2>API <small>RESTful &amp; JSON</small></h2>
		</div>
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th>Resource</th>
					<th>Description</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<th colspan="2">Task</th>
				</tr>
				<tr>
					<td><code>GET</code> /api/task</td>
					<td>Returns array of all available tasks</td>
				</tr>
				<tr>
					<td><code>GET</code> /api/task/count</td>
					<td>Returns <code>int</code> count of all available tasks</td>
				</tr>
				<tr>
					<td><code>GET</code> /api/task/:id</td>
					<td>Returns existing task by :id</td>
				</tr>
				<tr>
					<td><code>POST</code> /api/task</td>
					<td>Creates new task; request requirements are <code>Content-Type: application/json</code> header, and new <code><a href="#task">Task</a></code> in request body</td>
				</tr>
				<tr>
					<td><code>PUT</code> /api/task/:id</td>
					<td>Updates existing task by :id; request requirements are <code>Content-Type: application/json</code> header, and existing <code><a href="#task">Task</a></code> in request body</td>
				</tr>
				<tr>
					<td><code>DELETE</code> /api/task/:id</td>
					<td>Deletes (then returns) existing task by :id</td>
				</tr>
				<tr>
					<td><code>GET</code> /api/task/requestType</td>
					<td>Returns array of all available (task) request types</td>
				</tr>
				<tr>
					<td><code>GET</code> /api/task/scheduleType</td>
					<td>Returns array of all available (task) schedule types</td>
				</tr>
				<tr>
					<th colspan="2">Task Scheduling</th>
				</tr>
				<tr>
					<td><code>GET</code> /api/schedule/:taskId</td>
					<td>Checks if task is scheduled by :taskId; returns <code>boolean</code></td>
				</tr>
				<tr>
					<td><code>POST</code> /api/schedule/:taskId</td>
					<td>Schedules task by :taskId; returns <code><a href="#schedule-status">Schedule Status</a></code></td>
				</tr>
				<tr>
					<td><code>DELETE</code> /api/schedule/:taskId</td>
					<td>Cancels scheduled task by :taskId; returns <code><a href="#schedule-status">Schedule Status</a></code></td>
				</tr>
				<tr>
					<th colspan="2">Task Result</th>
				</tr>
				<tr>
					<td><code>GET</code> /api/result/count</td>
					<td>Returns <code>int</code> count of all available task results; optional query parameter is <code>taskId</code></td>
				</tr>
				<tr>
					<td><code>GET</code> /api/result</td>
					<td>Returns array of task results; optional query parameters are <code>offset</code>,  default is 0, <code>limit</code>, default is 100, and <code>taskId</code></td>
				</tr>
<!-- 
				<tr>
					<td><code>GET</code> /api/result/:id</td>
					<td>Returns existing task result by :id</td>
				</tr>
				<tr>
					<td><code>DELETE</code> /api/result/:id</td>
					<td>Deletes (and returns) task result by :id</td>
				</tr>
 -->
				<tr>
					<td><code>DELETE</code> /api/result/:taskId</td>
					<td>Deletes all existing task results by :taskId</td>
				</tr>
			</tbody>
		</table>

		<div class="row">
			<div class="span12">
				<div class="alert">
					Once a task is created with <code>POST</code> request to
					<code>/api/task</code> resource, it can be scheduled by
					sending <code>POST</code> request to <code>/api/schedule/:taskId</code>
					resource. Scheduled task <strong>cannot be updated while running</strong>,
					but has to be canceled, then updated and <strong>re-scheduled</strong> again.
				</div>
			</div>
		</div>

		<div class="row">
			<section id="task"></section>
			<div class="span4">
				<h3>Task <small>Model Sample</small></h3>
				<p>
					<pre>{ id: 100, /* auto-generated */
  title: "Task sample",
  description: "Run every 30 seconds",
  requestType: "GET",
  requestUri: "http://your.service.uri",
  requestHeader: null,
  requestBody: null,
  scheduleType: "FIXED_RATE",
  schedule: "30000",
  scheduled: true, /* metadata */
  lastRun: 1332073289, /* metadata */
  nextRun: 1332173289, /* metadata */
  runningNow: false, /* metadata */
  serverId: null /* metadata */ }</pre>
				</p>
			</div>
			<div class="span4">
				<h3>Task Result <small>Model Sample</small></h3>
				<p>
					<pre>{ id: 200, /* auto-generated */
  taskId: 100,
  result: "...", /* HTTP response body */
  timestamp: 1332073289 }</pre>
				</p>
			</div>
			<section id="schedule-status"></section>
			<div class="span4">
				<h3>Schedule Status <small>Model Sample</small></h3>
				<p>
					<pre>{ message: "Task is scheduled",
  task: {...} /* <a href="#task">Task</a> */ }</pre>
				</p>
				<p>While scheduling tasks, in case exception occurs, exception message will be displayed within <code>message</code> element, and task' <code>scheduled</code> property will be set to <strong>false</strong>.</p>
			</div>
		</div>

		<section id="changelog"></section>
		<div class="page-header">
			<h2>Changelog <small>Snapshot &amp; Releases</small></h2>
		</div>
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th>Version</th>
					<th>Description</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><code>0.3.0-SNAPSHOT</code></td>
					<td>
						<ul class="unstyled">
							<li>Task model updated with new property: serverId</li>
							<li>UI updates</li>
						</ul>
					</td>
				</tr>
				<tr>
					<td><code>0.2.1-SNAPSHOT</code></td>
					<td>
						<ul class="unstyled">
							<li>API documentation update</li>
							<li>Cross-Origin Resource Sharing (CORS) filter added</li>
						</ul>
					</td>
				</tr>
				<tr>
					<td><code>0.2.0</code></td>
					<td>Release</td>
				</tr>
				<tr>
					<td><code>0.2.0-SNAPSHOT</code></td>
					<td>
						<ul class="unstyled">
							<li>UI fixes</li>
							<li>Task model updated with new properties: nextRun, runningNow</li>
						</ul>
					</td>
				</tr>
				<tr>
					<td><code>0.1.0</code></td>
					<td>Release</td>
				</tr>
				<tr>
					<td><code>0.1.0-SNAPSHOT</code></td>
					<td>
						<ul class="unstyled">
							<li>HTTP tasks management &amp; scheduling; Log preview &amp; cleanup</li>
							<li>UI support for all major browsers (tablets, smartphones)</li>
							<li>RESTful &amp; JSON API</li>
						</ul>
					</td>
				</tr>
				<tr>
					<td><code>0.0.0</code></td>
					<td>Big Bang! ... and then the first task were scheduled!</td>
				</tr>
			</tbody>
		</table>

		<footer class="footer">
			<p class="pull-right">
				<a href="#">Back to top</a>
			</p>
			<p>&copy; 2012 Infobip Ltd</p>
		</footer>

	</div><!-- container -->

	<script src="/static/jquery/jquery-1.7.1.min.js"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/ext-4.0.7-gpl/ext-all.js"></script>
	<script src="/static/infobip/js/ext-grid.js"></script>

<!--[if lt IE 9]>
	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

</body>
</html>
