attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;
varying vec2 v_screenCoords;
varying vec2 v_vecX, v_vecY;
varying vec4 v_color;

#define M_PI 3.1415927

float decodeFloatFromV2(vec2 vec) {
    return vec.x + vec.y / 256.0;
}

void main() {
    vec4 pos = u_projTrans * a_position;
    gl_Position = pos;
    v_screenCoords = 0.5 * (pos.xy + vec2(1));

    float angle = 2.0 * M_PI * decodeFloatFromV2(a_color.rg);
    v_vecX = vec2(cos(angle), sin(angle));

    v_texCoords = a_texCoord0;
    v_color = a_color;
}
