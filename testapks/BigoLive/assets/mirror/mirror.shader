#VERTEX_CODE

precision highp float;

attribute vec4 vert_position;
attribute vec4 text_coord;
varying   vec2 output_text_coord;

void main() {
    gl_Position       = vert_position;
    output_text_coord = text_coord.xy;
}


#END_CODE

#FRAGMENT_CODE

precision mediump float;

varying vec2 output_text_coord;
uniform sampler2D input_img;

void main()
{
   gl_FragColor = texture2D(input_img, vec2(1.0-output_text_coord.x,1.0-output_text_coord.y));

}
#END_CODE