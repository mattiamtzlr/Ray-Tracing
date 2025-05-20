#pragma once

typedef struct {
    double x, y, z;
} vec3_t;

vec3_t vec3_neg(vec3_t);

vec3_t vec3_add(vec3_t, vec3_t);
vec3_t vec3_sub(vec3_t, vec3_t);
vec3_t vec3_mul(vec3_t, vec3_t);

vec3_t vec3_add_s(vec3_t, double);
vec3_t vec3_sub_s(vec3_t, double);
vec3_t vec3_mul_s(vec3_t, double);
vec3_t vec3_div_s(vec3_t, double);

double vec3_len(vec3_t);
double vec3_len_sqr(vec3_t);

double dot(vec3_t, vec3_t);
vec3_t cross(vec3_t, vec3_t);
vec3_t vec3_unit(vec3_t);

void vec3_print(vec3_t);
