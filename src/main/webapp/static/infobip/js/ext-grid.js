/*
 * Task Schedule Grid
 */

Ext.MessageBox = function() {
	var box;
	function createBox(t, s) {
		return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
	};
	return {
		pop : function(title, format) {
			if (!box) {
				box = Ext.DomHelper.insertFirst(document.body, {id: 'msg-div'}, true);
			}
			var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
			var m = Ext.DomHelper.append(box, createBox(title, s), true);
			m.hide();
			m.slideIn('t').ghost('t', {delay: 2500, remove: true});
		}
	};
}();

// TODO Take from server
var requestType = ['GET', 'POST', 'PUT', 'DELETE'];

// TODO Take from server
var scheduleType = [
	["FIXED_DELAY", "Fixed Delay (ms)"],
	["FIXED_RATE", "Fixed Rate (ms)"],
	["CRON", "Crontab pattern"]
];

Ext.define('Task', {
	extend : 'Ext.data.Model',
	fields : [
//		{name : '@class', defaultValue : 'org.infobip.data.Task'},
		{name : 'id', type : 'int'},
		{name : 'title'},
		{name : 'description'},
		{name : 'requestType', defaultValue : 'GET'},
		{name : 'requestUri'},
		{name : 'requestHeader'},
		{name : 'requestBody'},
		{name : 'scheduleType', defaultValue : 'CRON'},
		{name : 'schedule'},
		{name : 'scheduled', type : 'boolean', defaultValue : false},
		{name : 'lastRun', type : 'long', defaultValue : 0},
		{name : 'nextRun', type : 'long', defaultValue : 0},
		{name : 'runningNow', type : 'boolean', defaultValue : false},
		{name : 'serverId'}
	]
});

Ext.define('TaskResult', {
	extend : 'Ext.data.Model',
	fields : [
//		{name : '@class', defaultValue : 'org.infobip.data.TaskResult'},
		{name : 'id', type : 'int'},
		{name : 'taskId', type : 'int'},
		{name : 'result'},
		{name : 'timestamp', type : 'long', defaultValue : 0}
	]
});

Ext.onReady(function() {

	Ext.tip.QuickTipManager.init();

	var taskProxy = {
		type : 'rest',
		url : '/api/task',
		reader : 'json',
		writer : 'json',
		noCache : false,
		pageParam : undefined,
		startParam : undefined,
		limitParam : undefined
	};

	var taskStore = Ext.create('Ext.data.Store', {
		autoLoad : true,
		model : 'Task',
		proxy : taskProxy
	});

	var resultProxy = {
		type : 'rest',
		url : '/api/result',
		reader : {
			type : 'json',
			root : 'items',
			totalProperty : 'count'
		},
		writer : 'json',
		noCache : false,
		pageParam : undefined,
		startParam : 'offset',
		extraParams : {taskId : ''},
		limitParam : 'limit'
	};

	var resultStore = Ext.create('Ext.data.Store', {
		autoLoad : false,
		model : 'TaskResult',
		proxy : resultProxy,
		pageSize : 100
	});

	// Fix for errorSummary, v4.0.7+
	Ext.override(Ext.grid.RowEditor, {
		loadRecord: function(record) {
			var me = this,
				form = me.getForm(),
				valid = form.isValid();
			form.loadRecord(record);
			if (me.errorSummary) {
				me[valid ? 'hideToolTip' : 'showToolTip']();
			}
			Ext.Array.forEach(me.query('>displayfield'), function(field) {
				me.renderColumnData(field, record);
			}, me);
		}
	});

	var rowEditor = Ext.create('Ext.grid.plugin.RowEditing', {
		clicksToMoveEditor : 1,
		errorSummary : false
	});

	rowEditor.addListener('beforeedit', function(rowEditor, rowIndex) {
		// Disable all buttons
		grid.down('#btn-sync').setDisabled(true);
		grid.down('#btn-add').setDisabled(true);
		grid.down('#btn-delete').setDisabled(true);
		grid.down('#btn-schedule').setDisabled(true);
		grid.down('#btn-cancel').setDisabled(true);
		grid.down('#btn-log').setDisabled(true);

//		var item = grid.getView().getSelectionModel().getSelection()[0];
//		if (item && item.get('scheduled') === true) {
//			Ext.MessageBox.pop('Warning', 'Task cannot be udated while running');
//		}
	});

	rowEditor.addListener('afteredit', function(rowEditor, rowIndex) {

		var item = grid.getView().getSelectionModel().getSelection()[0];
		if (item) {
			if (item.get('scheduled') === true) {
				Ext.MessageBox.pop('Error', 'Task cannot be udated while running');
//				taskStore.load();
			} else {
				taskStore.save();
			}
		} else {
			taskStore.removeAt(0);
		}

		grid.down('#btn-sync').setDisabled(false);
		grid.down('#btn-add').setDisabled(false);
		grid.down('#btn-delete').setDisabled(false);
		grid.down('#btn-schedule').setDisabled(item.get('scheduled'));
		grid.down('#btn-cancel').setDisabled(!item.get('scheduled'));
		grid.down('#btn-log').setDisabled(false);
	});

	rowEditor.addListener('canceledit', function(rowEditor, rowIndex) {

		var item = grid.getView().getSelectionModel().getSelection()[0];
		if (item.get('id') === 0) {
			taskStore.removeAt(0);
			grid.down('#btn-delete').setDisabled(true);
			grid.down('#btn-schedule').setDisabled(true);
			grid.down('#btn-cancel').setDisabled(true);
			grid.down('#btn-log').setDisabled(true);
		} else {
			grid.down('#btn-delete').setDisabled(false);
			grid.down('#btn-schedule').setDisabled(item.get('scheduled'));
			grid.down('#btn-cancel').setDisabled(!item.get('scheduled'));
			grid.down('#btn-log').setDisabled(false);
		}

		grid.down('#btn-sync').setDisabled(false);
		grid.down('#btn-add').setDisabled(false);
	});

	var toolbar = {
		xtype : 'toolbar',
		layout : {
			overflowHandler : 'Menu'
		},
		items : [{
			itemId : 'btn-sync',
			text : 'Sync',
			tooltip : 'Data syncronization',
			iconCls : 'icon-sync',
			handler : function() {
				taskStore.load();
				Ext.MessageBox.pop('Sync', 'Data is syncronized');
			}
		}, {
			itemId : 'btn-add',
			text : 'Add',
			tooltip : 'Add new task',
			iconCls : 'icon-add',
			handler : function() {
				taskStore.insert(0, new Task());
				rowEditor.startEdit(0, 0);
			}
		}, {
			itemId : 'btn-delete',
			text : 'Delete',
			tooltip : 'Delete selected task',
			iconCls : 'icon-delete',
			disabled : true,
			handler : function() {
				var item = grid.getView().getSelectionModel().getSelection()[0];
				if (item) {
					Ext.Msg.confirm('Confirm', 'Are you sure you want to delete selected item?', function(response) {
						if (response == 'yes') {
							taskStore.remove(item);
							taskStore.save();
						}
					});
				}
			}
		}, {
			itemId : 'btn-schedule',
			text : 'Schedule',
			tooltip : 'Schedule selected task',
			iconCls : 'icon-schedule',
			disabled : true,
			handler : function() {
			var item = grid.getView().getSelectionModel().getSelection()[0];
		
		//problem: when multiple records are selected, only the first row selected executes its job
				
		//	var dataToSend = new Array();						first potential solution
		//		for (var i = 0; i < selection.length; i++) {
		//		dataToSend.push(selection[i]);
		//				}
		//									
		//	selected = [];										second potential solution
		//	Ext.each(s, function (item) {
		//	selected.push(item.data.someField);
		//								}); 
				
				
				if (item) {
					Ext.Ajax.request({
						method : 'POST',
						url : Ext.String.format('/api/schedule/{0}', item.get('id')),
						success : function(response) {
							var obj = Ext.JSON.decode(response.responseText);
							var msg = Ext.String.format('{0} : {1}', obj.message, obj.task.title);
							Ext.MessageBox.pop('Schedule', msg);
							taskStore.load();
						},
						failure : function(response) {
							Ext.MessageBox.pop('Error', response.responseText);
						}
					});
				}
			}
		}, {
			itemId : 'btn-cancel',
			text : 'Cancel',
			tooltip : 'Cancel selected task',
			iconCls : 'icon-cancel',
			disabled : true,
			handler : function() {
				var item = grid.getView().getSelectionModel().getSelection()[0];
				if (item) {
					Ext.Msg.confirm('Confirm', 'Are you sure you want to cancel selected item?', function(response) {
						if (response == 'yes') {
							Ext.Ajax.request({
								method : 'DELETE',
								url : Ext.String.format('/api/schedule/{0}', item.get('id')),
								success : function(response) {
									var obj = Ext.JSON.decode(response.responseText);
									var msg = Ext.String.format('{0} : {1}', obj.message, obj.task.title);
									Ext.MessageBox.pop('Cancel', msg);
									taskStore.load();
								},
								failure : function(response) {
									Ext.MessageBox.pop('Error', response.responseText);
								}
							});
						}
					});
				}
			}
		}, {
			itemId : 'btn-log',
			text : 'Log',
			tooltip : 'Open log',
			iconCls : 'icon-log',
			disabled : true,
			handler : function() {
				var item = grid.getView().getSelectionModel().getSelection()[0];
				if (item) {
					resultStore.getProxy().extraParams.taskId = item.get('id');
					resultStore.load();
					Ext.create('Ext.Window', {
						title : Ext.String.format('Log - {0}', item.get('title')),
						width : grid.getWidth() * 90 / 100,
						height : 350,
						plain : true,
//						maximizable : true,
						layout : 'fit',
						items : {
							border : false,
							xtype : 'grid',
							store : resultStore,
							columns : [{
								text : 'ID',
								dataIndex : 'id',
								width : 40,
								sortable : true,
								hidden : true
							}, {
								text : 'Time',
								dataIndex : 'timestamp',
								width : 150,
								sortable : true,
								renderer : function(value) {
									if (value === 0) {
										return '';
									}
									var date = new Date();
									date.setTime(value);
									return Ext.Date.format(date, 'Y-m-d H:i:s');
								}
							}, {
								text : 'Result',
								dataIndex : 'result',
								flex : 1,
								renderer : function(value) {
									value = Ext.util.Format.htmlEncode(value);
									value = value.replace(/\n/g, '<br>');
									return '<div style="white-space: normal !important;">' + value + '</div>';
								}
							}],
							dockedItems : [{
								xtype : 'pagingtoolbar',
								store : resultStore,
								dock : 'bottom',
								displayInfo : true,
								items : ['-', {
									text : 'Clear log',
									tooltip : 'Permanently delete all items',
									handler : function() {
										var item = grid.getView().getSelectionModel().getSelection()[0];
										if (item) {
											Ext.Msg.confirm('Confirm', 'Are you sure you want to delete all items?', function(response) {
												if (response == 'yes') {
													Ext.Ajax.request({
														method : 'DELETE',
														url : Ext.String.format('/api/result/{0}', item.get('id')),
														success : function(response) {
//															var obj = Ext.JSON.decode(response.responseText);
//															var msg = Ext.String.format('{0} : {1}', obj.message, obj.task.title);
//															Ext.MessageBox.pop('Delete', msg);
															resultStore.load();
														},
														failure : function(response) {
															Ext.MessageBox.pop('Error', response.responseText);
														}
													});
												}
											});
										}
									}
								}]
							}]
						}
					}).show();
				}
			}
		}]
	};
	
	var sm = Ext.create('Ext.selection.CheckboxModel',{
    checkOnly:true });
	var grid = Ext.create('Ext.grid.Panel', {
		
		store : taskStore,
		selModel: sm,
		columns : [{
			text : 'ID',
			dataIndex : 'id',
			width : 40,
			sortable : true,
			hidden : true
		}, {
			text : 'Title',
			dataIndex : 'title',
			flex : 1,
			sortable : true,
			editor : {
				allowBlank : false
			}
		}, {
			header : 'Description',
			dataIndex : 'description',
			flex : 1,
			sortable : true,
			field : {
				xtype : 'textfield'
			}
		}, {
			text : 'Request Type',
			dataIndex : 'requestType',
			flex : 1,
			sortable : true,
			editor : {
				xtype : 'combobox',
				editable : false,
				typeAhead : true,
				triggerAction : 'all',
				selectOnTab : true,
				store : requestType,
				lazyRender : true,
//				listClass : 'x-combo-list-small' // TODO Fix the css
				displayField : 'display',
				valueField : 'value'
			},
			renderer : function(value) {
				var display = '';
				$.each(requestType, function(index, item) {
					if (value == item) {
						display = item;
					}
				});
				return display;
			}
		}, {
			text : 'Request URI',
			dataIndex : 'requestUri',
			flex : 1,
			sortable : true,
			editor : {
				allowBlank : false
			}
		}, {
			text : 'Request Header',
			dataIndex : 'requestHeader',
			flex : 1,
			sortable : true,
			field : {
				xtype : 'textfield'
			}
		}, {
			text : 'Request Body',
			dataIndex : 'requestBody',
			flex : 1,
			sortable : true,
			field : {
				xtype : 'textfield'
			}
		}, {
			header : 'Schedule Type',
			dataIndex : 'scheduleType',
			flex : 1,
			sortable : true,
			editor : {
				xtype : 'combobox',
				editable : false,
				typeAhead : true,
				triggerAction : 'all',
				selectOnTab : true,
				store : scheduleType,
				lazyRender : true,
//				listClass : 'x-combo-list-small' // TODO Fix the css
				displayField : 'display',
				valueField : 'value'
			},
			renderer : function(value) {
				var display = '';
				$.each(scheduleType, function(index, item) {
					if (value == item[0]) {
						display = item[1];
					}
				});
				return display;
			}
		}, {
			text : 'Schedule',
			dataIndex : 'schedule',
			flex : 1,
			sortable : true,
			editor : {
				allowBlank : false
			}
		}, {
			text : 'Scheduled',
			dataIndex : 'scheduled',
			flex : 1,
			sortable : true,
			renderer : function(value) {
				return value === true ? 'Yes' : 'No';
			}
		}, {
			text : 'Last Run',
			dataIndex : 'lastRun',
			flex : 1,
			sortable : true,
			renderer : function(value) {
				if (value === 0) {
					return '';
				}
				var date = new Date();
				date.setTime(value);
				return Ext.Date.format(date, 'Y-m-d H:i:s');
			}
		}, {
			text : 'Next Run',
			dataIndex : 'nextRun',
			flex : 1,
			sortable : true,
			renderer : function(value) {
				if (value === 0) {
					return '';
				}
				var date = new Date();
				date.setTime(value);
				return Ext.Date.format(date, 'Y-m-d H:i:s');
			}
		}, {
			text : 'Running Now',
			dataIndex : 'runningNow',
			flex : 1,
			sortable : true,
			renderer : function(value) {
				return value === true ? 'Yes' : 'No';
			}
		}, {
			text : 'Server ID',
			dataIndex : 'serverId',
			flex : 1,
			sortable : true
		}],
		columnLines: true,
		height : 350,
		title : 'Tasks',
//		iconCls : 'icon-cog',
		layout : 'fit',
		autoScroll: true,
		plugins : [rowEditor],
		dockedItems : [toolbar],
		renderTo : 'grid-div'
	});
	

	// TODO Handle toolbar actions from one place
	grid.getSelectionModel().on('selectionchange', function(selModel, selections) {

		var disabled = (selections.length === 0 || grid.down('#btn-add').isDisabled());
		var item = grid.getView().getSelectionModel().getSelection()[0];

		grid.down('#btn-delete').setDisabled(disabled || item.get('scheduled'));
		grid.down('#btn-schedule').setDisabled(disabled || item.get('scheduled'));
		grid.down('#btn-cancel').setDisabled(disabled || !item.get('scheduled'));
		grid.down('#btn-log').setDisabled(disabled);
	});

	Ext.EventManager.onWindowResize(grid.doLayout, grid);

});
