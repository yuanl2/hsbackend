(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-d9cd"],{"18ca":function(t,e,n){},"30d9":function(t,e,n){},4974:function(t,e,n){"use strict";var a=n("bbb3"),i=n.n(a);i.a},"7e1e":function(t,e,n){"use strict";n.d(e,"j",function(){return i}),n.d(e,"b",function(){return o}),n.d(e,"a",function(){return r}),n.d(e,"c",function(){return l}),n.d(e,"d",function(){return s}),n.d(e,"e",function(){return c}),n.d(e,"h",function(){return u}),n.d(e,"g",function(){return d}),n.d(e,"f",function(){return h}),n.d(e,"k",function(){return p}),n.d(e,"i",function(){return f});var a=n("66df"),i=function(){return a["a"].request({url:"/api/provinces",method:"get"})},o=function(){return a["a"].request({url:"/api/citys",method:"get"})},r=function(){return a["a"].request({url:"/api/locations",method:"get"})},l=function(){return a["a"].request({url:"/api/consumes",method:"get"})},s=function(t){return a["a"].request({url:"/api/devices/",method:"get",params:{userID:t}})},c=function(t){return a["a"].request({url:"/api/devices/fault",method:"get",params:{userID:t}})},u=function(t,e,n,i){return a["a"].request({url:"/api/order/statics",method:"get",params:{user:t,type:e,startTime:n,endTime:i}})},d=function(t,e){return a["a"].request({url:"/api/order/notfinish",method:"get",params:{user:t,startTime:e}})},h=function(t,e){return a["a"].request({url:"/api/order/finish",method:"get",params:{user:t,startTime:e}})},p=function(t){return a["a"].request({url:"/api/order/summary",method:"get",params:{user:t}})},f=function(t){return a["a"].request({url:"/api/order/summaryforhour",method:"get",params:{user:t}})}},ac96:function(t,e,n){"use strict";var a=n("18ca"),i=n.n(a);i.a},bbb3:function(t,e,n){},ecf7:function(t,e,n){"use strict";n.r(e);var a=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[n("Card",[n("tables",{ref:"tables",attrs:{editable:"",searchable:"","search-place":"top",columns:t.columns},on:{"on-delete":t.handleDelete},model:{value:t.tableData,callback:function(e){t.tableData=e},expression:"tableData"}}),n("Button",{staticStyle:{margin:"10px 0"},attrs:{type:"primary"},on:{click:t.exportExcel}},[t._v("导出为Excel文件")])],1)],1)},i=[],o=(n("cadf"),n("551c"),n("097d"),n("fa69")),r=n("7e1e"),l={name:"tables_page_area_user",components:{Tables:o["a"]},data:function(){return{columns:[{title:"网点ID",key:"id",sortable:!0},{title:"网点名称",key:"areaName",sortable:!0},{title:"网点地址",key:"address"},{title:"省/直辖市",key:"province"},{title:"城市",key:"city"},{title:"进场时间",key:"enterTime"}],tableData:[],timeRanges:[]}},methods:{handleDelete:function(t){console.log(t)},exportExcel:function(){this.$refs.tables.exportCsv({filename:"table-".concat((new Date).valueOf(),".xlsx")})}},mounted:function(){var t=this;Object(r["a"])().then(function(e){t.tableData=e})},watch:{timeRanges:function(t){var e=this;Object(r["a"])().then(function(t){e.tableData=t,console.log(t)})}}},s=l,c=(n("ac96"),n("2877")),u=Object(c["a"])(s,a,i,!1,null,null,null);u.options.__file="area.vue";e["default"]=u.exports},fa69:function(t,e,n){"use strict";var a=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[t.searchable&&"top"===t.searchPlace?n("div",{staticClass:"search-con search-con-top"},[n("Select",{staticClass:"search-col",model:{value:t.searchKey,callback:function(e){t.searchKey=e},expression:"searchKey"}},t._l(t.columns,function(e){return"handle"!==e.key?n("Option",{key:"search-col-"+e.key,attrs:{value:e.key}},[t._v(t._s(e.title))]):t._e()})),n("Input",{staticClass:"search-input",attrs:{clearable:"",placeholder:"输入关键字搜索"},on:{"on-change":t.handleClear},model:{value:t.searchValue,callback:function(e){t.searchValue=e},expression:"searchValue"}}),n("Button",{staticClass:"search-btn",attrs:{type:"primary"},on:{click:t.handleSearch}},[n("Icon",{attrs:{type:"search"}}),t._v("  搜索")],1)],1):t._e(),n("Table",{ref:"tablesMain",attrs:{data:t.insideTableData,columns:t.insideColumns,stripe:t.stripe,border:t.border,"show-header":t.showHeader,width:t.width,height:t.height,loading:t.loading,"disabled-hover":t.disabledHover,"highlight-row":t.highlightRow,"row-class-name":t.rowClassName,size:t.size,"no-data-text":t.noDataText,"no-filtered-data-text":t.noFilteredDataText},on:{"on-current-change":t.onCurrentChange,"on-select":t.onSelect,"on-select-cancel":t.onSelectCancel,"on-select-all":t.onSelectAll,"on-selection-change":t.onSelectionChange,"on-sort-change":t.onSortChange,"on-filter-change":t.onFilterChange,"on-row-click":t.onRowClick,"on-row-dblclick":t.onRowDblclick,"on-expand":t.onExpand}},[t._t("header",null,{slot:"header"}),t._t("footer",null,{slot:"footer"}),t._t("loading",null,{slot:"loading"})],2),t.searchable&&"bottom"===t.searchPlace?n("div",{staticClass:"search-con search-con-top"},[n("Select",{staticClass:"search-col",model:{value:t.searchKey,callback:function(e){t.searchKey=e},expression:"searchKey"}},t._l(t.columns,function(e){return"handle"!==e.key?n("Option",{key:"search-col-"+e.key,attrs:{value:e.key}},[t._v(t._s(e.title))]):t._e()})),n("Input",{staticClass:"search-input",attrs:{placeholder:"输入关键字搜索"},model:{value:t.searchValue,callback:function(e){t.searchValue=e},expression:"searchValue"}}),n("Button",{staticClass:"search-btn",attrs:{type:"primary"}},[n("Icon",{attrs:{type:"search"}}),t._v("  搜索")],1)],1):t._e(),n("a",{staticStyle:{display:"none",width:"0px",height:"0px"},attrs:{id:"hrefToExportTable"}})],1)},i=[],o=(n("ac6a"),n("f751"),n("c5f6"),function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"tables-edit-outer"},[t.isEditting?n("div",{staticClass:"tables-editting-con"},[n("Input",{staticClass:"tables-edit-input",attrs:{value:t.value},on:{input:t.handleInput}}),n("Button",{staticStyle:{padding:"6px 4px"},attrs:{type:"text"},on:{click:t.saveEdit}},[n("Icon",{attrs:{type:"md-checkmark"}})],1),n("Button",{staticStyle:{padding:"6px 4px"},attrs:{type:"text"},on:{click:t.canceltEdit}},[n("Icon",{attrs:{type:"md-close"}})],1)],1):n("div",{staticClass:"tables-edit-con"},[n("span",{staticClass:"value-con"},[t._v(t._s(t.value))]),t.editable?n("Button",{staticClass:"tables-edit-btn",staticStyle:{padding:"2px 4px"},attrs:{type:"text"},on:{click:t.startEdit}},[n("Icon",{attrs:{type:"md-create"}})],1):t._e()],1)])}),r=[],l=(n("cadf"),n("551c"),n("097d"),{name:"TablesEdit",props:{value:[String,Number],edittingCellId:String,params:Object,editable:Boolean},computed:{isEditting:function(){return this.edittingCellId==="editting-".concat(this.params.index,"-").concat(this.params.column.key)}},methods:{handleInput:function(t){this.$emit("input",t)},startEdit:function(){this.$emit("on-start-edit",this.params)},saveEdit:function(){this.$emit("on-save-edit",this.params)},canceltEdit:function(){this.$emit("on-cancel-edit",this.params)}}}),s=l,c=(n("4974"),n("2877")),u=Object(c["a"])(s,o,r,!1,null,null,null);u.options.__file="edit.vue";var d=u.exports,h={delete:function(t,e,n){return t("Poptip",{props:{confirm:!0,title:"你确定要删除吗?"},on:{"on-ok":function(){n.$emit("on-delete",e),n.$emit("input",e.tableData.filter(function(t,n){return n!==e.row.initRowIndex}))}}},[t("Button",{props:{type:"text",ghost:!0}},[t("Icon",{props:{type:"md-trash",size:18,color:"#000000"}})])])}},p=h,f=(n("30d9"),{name:"Tables",props:{value:{type:Array,default:function(){return[]}},columns:{type:Array,default:function(){return[]}},size:String,width:{type:[Number,String]},height:{type:[Number,String]},stripe:{type:Boolean,default:!1},border:{type:Boolean,default:!1},showHeader:{type:Boolean,default:!0},highlightRow:{type:Boolean,default:!1},rowClassName:{type:Function,default:function(){return""}},context:{type:Object},noDataText:{type:String},noFilteredDataText:{type:String},disabledHover:{type:Boolean},loading:{type:Boolean,default:!1},editable:{type:Boolean,default:!1},searchable:{type:Boolean,default:!1},searchPlace:{type:String,default:"top"}},data:function(){return{insideColumns:[],insideTableData:[],edittingCellId:"",edittingText:"",searchValue:"",searchKey:""}},methods:{suportEdit:function(t,e){var n=this;return t.render=function(t,e){return t(d,{props:{params:e,value:n.insideTableData[e.index][e.column.key],edittingCellId:n.edittingCellId,editable:n.editable},on:{input:function(t){n.edittingText=t},"on-start-edit":function(t){n.edittingCellId="editting-".concat(t.index,"-").concat(t.column.key),n.$emit("on-start-edit",t)},"on-cancel-edit":function(t){n.edittingCellId="",n.$emit("on-cancel-edit",t)},"on-save-edit":function(t){n.value[t.row.initRowIndex][t.column.key]=n.edittingText,n.$emit("input",n.value),n.$emit("on-save-edit",Object.assign(t,{value:n.edittingText})),n.edittingCellId=""}}})},t},surportHandle:function(t){var e=this,n=t.options||[],a=[];n.forEach(function(t){p[t]&&a.push(p[t])});var i=t.button?[].concat(a,t.button):a;return t.render=function(t,n){return n.tableData=e.value,t("div",i.map(function(a){return a(t,n,e)}))},t},handleColumns:function(t){var e=this;this.insideColumns=t.map(function(t,n){var a=t;return a.editable&&(a=e.suportEdit(a,n)),"handle"===a.key&&(a=e.surportHandle(a)),a})},setDefaultSearchKey:function(){this.searchKey="handle"!==this.columns[0].key?this.columns[0].key:this.columns.length>1?this.columns[1].key:""},handleClear:function(t){""===t.target.value&&(this.insideTableData=this.value)},handleSearch:function(){var t=this;this.insideTableData=this.value.filter(function(e){return e[t.searchKey].indexOf(t.searchValue)>-1})},handleTableData:function(){this.insideTableData=this.value.map(function(t,e){var n=t;return n.initRowIndex=e,n})},exportCsv:function(t){this.$refs.tablesMain.exportCsv(t)},clearCurrentRow:function(){this.$refs.talbesMain.clearCurrentRow()},onCurrentChange:function(t,e){this.$emit("on-current-change",t,e)},onSelect:function(t,e){this.$emit("on-select",t,e)},onSelectCancel:function(t,e){this.$emit("on-select-cancel",t,e)},onSelectAll:function(t){this.$emit("on-select-all",t)},onSelectionChange:function(t){this.$emit("on-selection-change",t)},onSortChange:function(t,e,n){this.$emit("on-sort-change",t,e,n)},onFilterChange:function(t){this.$emit("on-filter-change",t)},onRowClick:function(t,e){this.$emit("on-row-click",t,e)},onRowDblclick:function(t,e){this.$emit("on-row-dblclick",t,e)},onExpand:function(t,e){this.$emit("on-expand",t,e)}},watch:{columns:function(t){this.handleColumns(t),this.setDefaultSearchKey()},value:function(t){this.handleTableData(),this.handleSearch()}},mounted:function(){this.handleColumns(this.columns),this.setDefaultSearchKey(),this.handleTableData()}}),m=f,b=Object(c["a"])(m,a,i,!1,null,null,null);b.options.__file="tables.vue";var g=b.exports;e["a"]=g}}]);