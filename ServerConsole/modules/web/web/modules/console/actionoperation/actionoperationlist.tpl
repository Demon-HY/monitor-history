<div class="data-toolbar">
    <button class="btn btn-info add-actionoperation">{{t 'consoleActionOperation_add_action_operation'}}</button>
    <button class="btn btn-info edit-actionoperation">{{t 'consoleActionOperation_edit_action_operation'}}</button>
    <button class="btn btn-info delete-actionoperation">{{t 'consoleActionOperation_delete_action_operation'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleActionOperation_actionoperation_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleActionOperation_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleActionOperation_reset'}}</button>
</div>

<div id="actionoperationlist" class="actionoperationlist"></div>