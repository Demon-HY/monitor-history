<div class="data-toolbar">
    <button class="btn btn-info add-trigger">{{t 'consoleTrigger_add_trigger'}}</button>
    <button class="btn btn-info edit-trigger">{{t 'consoleTrigger_edit_trigger'}}</button>
    <button class="btn btn-info delete-trigger">{{t 'consoleTrigger_delete_trigger'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleTrigger_trigger_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleTrigger_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleTrigger_reset'}}</button>
</div>

<div id="triggerlist" class="triggerlist"></div>