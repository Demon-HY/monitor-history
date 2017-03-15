{{#each rows}}
<div class="nav-item" name="{{name}}">
    <i class="nav-icon fa {{cls}}"></i>
    <span>{{text}}</span>
    {{#arrayLength children}}
    <i class="nav-arrow fa fa-caret-right"></i>
    {{/arrayLength}}
</div>
{{#arrayLength children}}
<div class="nav-child">
    {{#each children}}
    <div class="nav-item" name="{{name}}">
        <span>{{text}}</span>
    </div>
    {{/each}}
</div>
{{/arrayLength}}
{{/each}}