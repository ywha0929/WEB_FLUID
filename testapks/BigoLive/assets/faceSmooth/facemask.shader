#VERTEX_CODE

precision highp float;

attribute vec4 vert_position;
attribute vec4 text_coord;
varying   vec2 mask_coord;

void main() {

    gl_Position = vert_position;
    mask_coord  = text_coord.xy;
}

#END_CODE

#FRAGMENT_CODE

precision mediump float;

varying vec2      mask_coord;
uniform sampler2D face_mask;
uniform int       face_num;

void main()
{
    vec3 faceMask = texture2D(face_mask, mask_coord).rgb;
    gl_FragColor = vec4(faceMask.rgb, face_num > 1 ? 0.8 : 0.2);
}

#END_CODE