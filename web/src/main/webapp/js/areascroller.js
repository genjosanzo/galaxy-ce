Effect.Scroll = Class.create();
Object.extend(Object.extend(Effect.Scroll.prototype, Effect.Base.prototype), {
    initialize: function(element, options) {
        this.element = $(element);
        this.direction = options && options.direction ? options.direction : 'right';
        this.pps = options && options.pps ? options.pps : 100;

        var options = Object.extend({
            duration: (this.element.scrollWidth - this.element.scrollLeft) / this.pps
        }, arguments[1] || {});


        this.start(options);
    },
    setup: function() {
        this.scrollStart = this.element.scrollLeft;
        this.delta = this.element.scrollWidth;
    },
    update: function(position) {
        if (this.direction == 'right')
            this.element.scrollLeft = this.scrollStart + (position * this.delta);
        else
            this.element.scrollLeft = this.scrollStart - (position * this.delta);
    }
});


var rightScroller;
var leftScroller;

function setupScrollers(container) {
    var containerID = container;
    var r = {
        '#right_scroller': function(e) {
            e.onmouseover = function(e) {
                if (leftScroller) leftScroller.cancel();
                rightScroller = new Effect.Scroll(containerID, {pps: 100, direction: 'right'});
            }
            e.onmouseout = function(e) {
                if (rightScroller) {
                    rightScroller.cancel();
                }
            }
            e.onmousedown = function(e) {
                if (rightScroller) rightScroller.cancel();
                rightScroller = new Effect.Scroll(containerID, {pps: 1000, direction: 'right'});
            }
            e.onmouseup = function(e) {
                if (rightScroller) rightScroller.cancel();
                rightScroller = new Effect.Scroll(containerID, {pps: 100, direction: 'right'});
            }
        },
        '#left_scroller': function(e) {
            e.onmouseover = function(e) {
                if (rightScroller) rightScroller.cancel();
                leftScroller = new Effect.Scroll(containerID, {pps: 100, direction: 'left'});
            }
            e.onmouseout = function(e) {
                if (leftScroller) {
                    leftScroller.cancel();
                }
            }
            e.onmousedown = function(e) {
                if (leftScroller) leftScroller.cancel();
                leftScroller = new Effect.Scroll(containerID, {pps: 1000, direction: 'left'});
            }
            e.onmouseup = function(e) {
                if (leftScroller) leftScroller.cancel();
                leftScroller = new Effect.Scroll(containerID, {pps: 100, direction: 'left'});
            }
        }
    }

    Behaviour.register(r);
}
