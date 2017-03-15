/* multiselect.js 左右框选择插件 */
;(function($){
    $.fn.multiselect = function(options){

        let opts = $.extend({}, $.fn.multiselect.defaults, options);

        this.fill = function () {
            let option = '';
            $.each(opts.data, function (key, val) {
                option += '<option data-id=' + val.id + '>' + val.text + '</option>';
            });
            $('.available').append(option);
        };

        this.controll = function () {
            $this = $(this);
            
            $('.mul-add').click(function(event) {
                let p = $this.find(".available option:selected");
                p.clone().appendTo($this.find(".chosen"));
                p.remove();
            });
            
            $('.mul-addAll').click(function(event) {
                let p = $this.find(".available option");
                p.clone().appendTo($this.find(".chosen"));
                p.remove();
            });
            
            $('.mul-remove').click(function(event) {
                let p = $this.find(".chosen option:selected");
                p.clone().appendTo($this.find(".available"));
                p.remove();
            });
            
            $('.mul-removeAll').click(function(event) {
                let p = $this.find(".chosen option");
                p.clone().appendTo($this.find(".available"));
                p.remove();
            });
        };
        
        this.getValues = function() {
            let result = [];
            $this.find('.chosen option').each(function(){
                result.push({
                    id: $(this).data('id'),
                    text: this.text
                });
            });
            
            return result;
        }

        this.init = function () {
            let html =
                "<div class='mul-rows'>" +
                "   <div class='mul-available'>" +
                "       <select class='mul-selectd available' multiple></select>" +
                "   </div>" +
                "   <div class='mul-btn'>" +
                "       <button class='mul-add btn btn-primary' type='button'>" + opts.add + "</button>" +
                "       <button class='mul-addAll btn btn-primary' type='button'>" + opts.addAll + "</button>" +
                "       <button class='mul-remove btn btn-primary' type='button'>" + opts.remove + "</button>" +
                "       <button class='mul-removeAll btn btn-primary' type='button'>" + opts.removeAll + "</button>" +
                "   </div>" +
                "   <div class='mul-chosen'>" +
                "       <select class='mul-selectd chosen' multiple></select>" +
                "   </div>" +
                "</div>";
            this.append(html);

            this.fill();
            this.controll();
        };

        this.init();
        return this;
    };

    $.fn.multiselect.defaults = {
      add: '>',
      addAll: '>>',
      remove: '<',
      removeAll: '<<'
   };
})(jQuery);