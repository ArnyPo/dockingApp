
        from pymol import cmd,stored
        
        set depth_cue, 1
        set fog_start, 0.4
        
        set_color b_col, [36,36,85]
        set_color t_col, [10,10,10]
        set bg_rgb_bottom, b_col
        set bg_rgb_top, t_col      
        set bg_gradient
        
        set  spec_power  =  200
        set  spec_refl   =  0
        
        load "data/7RDY_engaged.pdb", protein
        create ligands, protein and organic
        select xlig, protein and organic
        delete xlig
        
        hide everything, all
        
        color white, elem c
        color bluewhite, protein
        #show_as cartoon, protein
        show surface, protein
        #set transparency, 0.15
        
        show sticks, ligands
        set stick_color, magenta
        
        
        
        
        # SAS points
 
        load "data/7RDY_engaged.pdb_points.pdb.gz", points
        hide nonbonded, points
        show nb_spheres, points
        set sphere_scale, 0.2, points
        cmd.spectrum("b", "green_red", selection="points", minimum=0, maximum=0.7)
        
        
        stored.list=[]
        cmd.iterate("(resn STP)","stored.list.append(resi)")    # read info about residues STP
        lastSTP=stored.list[-1] # get the index of the last residue
        hide lines, resn STP
        
        cmd.select("rest", "resn STP and resi 0")
        
        for my_index in range(1,int(lastSTP)+1): cmd.select("pocket"+str(my_index), "resn STP and resi "+str(my_index))
        for my_index in range(1,int(lastSTP)+1): cmd.show("spheres","pocket"+str(my_index))
        for my_index in range(1,int(lastSTP)+1): cmd.set("sphere_scale","0.4","pocket"+str(my_index))
        for my_index in range(1,int(lastSTP)+1): cmd.set("sphere_transparency","0.1","pocket"+str(my_index))
        
        
        
        set_color pcol1 = [0.361,0.576,0.902]
select surf_pocket1, protein and id [8730,7981,7996,7997,7968,7982,7984,6759,6762,8953,6765,6766,6767,6768,8941,6997,8732,8731,8733,7051,7461,7462,8707,8715,8716,8717,8718,7690,7692,8924,6786,6789,6785,6793,6794,6777,6779,6780,6788,6772,6776,7959,7961,7965,6799,6800,6801,6583,6586,6802,6803,6804,6581,7985,7986,7027,7028,7029,7050,7061,7062,6616,6609] 
set surface_color,  pcol1, surf_pocket1 
set_color pcol2 = [0.278,0.341,0.702]
select surf_pocket2, protein and id [4174,4175,4176,3426,3423,4412,2198,3411,3412,2196,2192,2217,2195,2197,2199,2206,2218,4159,4160,4161,3427,4396,2457,2458,2459,2225,2229,2219,2223,2224,2207,2210,2016,2230,2231,2234,2232,2233,2456,2481,2893,3122] 
set surface_color,  pcol2, surf_pocket2 
set_color pcol3 = [0.424,0.361,0.902]
select surf_pocket3, protein and id [7497,5618,5619,7513,7515,5620,6344,6341,6343,7355,7357,7524,7354,7356,5944,5650,5675,5676,5677,5945,5946,7718,7736,5640,5931] 
set surface_color,  pcol3, surf_pocket3 
set_color pcol4 = [0.435,0.278,0.702]
select surf_pocket4, protein and id [3144,3146,4136,4339,3136,3137,3214,4329,3134,3266,3128,3129,4287,4284,4314,4315,4295,4292,3183,3209,3211,3215,3208,3156] 
set surface_color,  pcol4, surf_pocket4 
set_color pcol5 = [0.698,0.361,0.902]
select surf_pocket5, protein and id [7495,7498,7829,5500,7817,7497,5493,5610,5586,5611,5464,5467,7728,7504,5587,7510,5553,5492,5584] 
set surface_color,  pcol5, surf_pocket5 
set_color pcol6 = [0.651,0.278,0.702]
select surf_pocket6, protein and id [8329,8599,6121,6122,6123,6152,6154,6164,8304,8316,8570,5901,5884,5883,5906,5904,8560,8561,8566,8567,8593,5905,5907,6120] 
set surface_color,  pcol6, surf_pocket6 
set_color pcol7 = [0.902,0.361,0.824]
select surf_pocket7, protein and id [3516,3380,3382,3553,3521,3524,2175,3100,3113,3518,3526,3328,3329,2187,3552,3563] 
set surface_color,  pcol7, surf_pocket7 
set_color pcol8 = [0.702,0.278,0.533]
select surf_pocket8, protein and id [1014,1038,2934,922,923,931,1042,1036,1016,1071,1040,1041,899,3155,895,897,3247,1070,1017,2940,2925,2927,2928,2926,3158,3259] 
set surface_color,  pcol8, surf_pocket8 
set_color pcol9 = [0.902,0.361,0.545]
select surf_pocket9, protein and id [8790,9014,8786,8788,8929,8936,8785,9002,8988] 
set surface_color,  pcol9, surf_pocket9 
set_color pcol10 = [0.702,0.278,0.318]
select surf_pocket10, protein and id [518,520,329,335,485,486,487,498,676,677,689,670,679,360,362,499] 
set surface_color,  pcol10, surf_pocket10 
set_color pcol11 = [0.902,0.451,0.361]
select surf_pocket11, protein and id [5384,5404,5392,4699,4691,5425,5632,5603,4704,4706,5631,4697,4694] 
set surface_color,  pcol11, surf_pocket11 
set_color pcol12 = [0.702,0.459,0.278]
select surf_pocket12, protein and id [5537,5539,4594,4607,4736,4742,5595,4713,5596,4712,5573,4721,4730,4710,6377,6371] 
set surface_color,  pcol12, surf_pocket12 
set_color pcol13 = [0.902,0.729,0.361]
select surf_pocket13, protein and id [8389,8778,8780,8647,8648,8428,8471,8636,8429,8490] 
set surface_color,  pcol13, surf_pocket13 
set_color pcol14 = [0.702,0.675,0.278]
select surf_pocket14, protein and id [2047,2011,2039,2230,2232,2233,2046,2207,3387,3390,2202,2229,3412,3415,3416,3413] 
set surface_color,  pcol14, surf_pocket14 
   
        
        deselect
        
        orient
        