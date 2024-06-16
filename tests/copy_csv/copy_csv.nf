process COPY_CSV {

  input:
    path zip

  output:
     path "output_*", emit: csv


  """
	cp ${zip} output_${zip.name}
  """

}
