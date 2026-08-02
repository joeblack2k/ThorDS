	.syntax unified
	.arm

	.global f42_world_entry
	.type f42_world_entry, %function
f42_world_entry:
	stmdb	sp!, {r2, lr}
	ldr	r12, [r0]
	ldr	r1, [r0, #0x9c]
	ldr	r2, .L_player_vtable
	cmp	r12, r2
	beq	.L_world_r1_return
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_world_r1_return
	mov	r2, r1, asr #1
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	moveq	r1, r2
	subne	r1, r1, r2
.L_world_r1_return:
	ldmia	sp!, {r2, pc}

	.global f42_world_entry_r4
	.type f42_world_entry_r4, %function
f42_world_entry_r4:
	stmdb	sp!, {r2, lr}
	ldr	r12, [r0]
	ldr	r4, [r0, #0x9c]
	ldr	r2, .L_player_vtable
	cmp	r12, r2
	beq	.L_world_r4_return
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_world_r4_return
	mov	r2, r4, asr #1
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	moveq	r4, r2
	subne	r4, r4, r2
.L_world_r4_return:
	ldmia	sp!, {r2, pc}

	.global f42_world_add_vec
	.type f42_world_add_vec, %function
f42_world_add_vec:
	ldr	r12, [r5]
	ldr	r3, .L_player_vtable
	cmp	r12, r3
	beq	.L_add_original
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_add_original
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	ldr	r3, [r0]
	ldr	r12, [r1]
	moveq	r12, r12, asr #1
	subne	r12, r12, r12, asr #1
	add	r12, r3, r12
	str	r12, [r2]
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	ldr	r3, [r0, #4]
	ldr	r12, [r1, #4]
	moveq	r12, r12, asr #1
	subne	r12, r12, r12, asr #1
	add	r12, r3, r12
	str	r12, [r2, #4]
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	ldr	r3, [r0, #8]
	ldr	r12, [r1, #8]
	moveq	r12, r12, asr #1
	subne	r12, r12, r12, asr #1
	add	r12, r3, r12
	str	r12, [r2, #8]
	bx	lr
.L_add_original:
	ldr	pc, .L_add_vec3

	.global f42_animation_speed
	.type f42_animation_speed, %function
f42_animation_speed:
	stmdb	sp!, {r1, lr}
	ldr	r0, [r4, #0xc]
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_animation_return
	mov	r1, r0, asr #1
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	moveq	r0, r1
	subne	r0, r0, r1
.L_animation_return:
	ldmia	sp!, {r1, pc}

	.global f42_particle_entry
	.type f42_particle_entry, %function
f42_particle_entry:
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_particle_original
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	bxeq	lr
.L_particle_original:
	stmdb	sp!, {r4, lr}
	ldr	pc, .L_particle_cont

	.align	2
.L_cadence:
	.word	0x0208EE44
.L_game_loop_counter:
	.word	0x020A0DB0
.L_player_vtable:
	.word	0x0210A83C
.L_add_vec3:
	.word	0x02053884
.L_particle_cont:
	.word	0x02022F24

	.size f42_world_entry, .-f42_world_entry
