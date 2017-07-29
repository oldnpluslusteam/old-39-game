attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_screenCoords;

void main() {
    vec4 pos = u_projTrans * a_position;
    gl_Position = pos;
    v_screenCoords = 0.5 * (pos.xy + vec2(1));

    v_color = a_color;
    v_texCoords = a_texCoord0;
}
